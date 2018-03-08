package com.stalex.pipeline

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import mu.KotlinLogging
import kotlin.coroutines.experimental.buildSequence

val logger = KotlinLogging.logger {}

interface Scrap

interface RefPage
class RefPageImpl(val url: String) : RefPage

interface RefItem
data class RefItemImpl(val id: String) : RefItem

interface Parser<in Source, out Target> {
    suspend fun parse(page: Source): Target
}

interface ScrapCollectionParser<in T : RefPage, out R : RefItem> : Parser<T, List<R>>
interface ScrapParser<in R : RefItem, out T : Scrap> : Parser<R, T>

interface RefPageProvider<out T : RefPage> {
    fun get(index: Int): T
}

interface LoaderFactory<out S : Scrap, P : RefPage, R : RefItem> {
    fun pageProvider(): RefPageProvider<P>
    fun itemProvider(): ScrapCollectionParser<P, R>
    fun loader(): ScrapParser<R, S>
}

fun <S : Scrap, P : RefPage, R : RefItem> LoaderFactory<S, P, R>.createSyncObservable(
    stopCondition: ((R) -> Boolean) = { false }
) = SyncObservable(
    pageProvider(),
    itemProvider(),
    loader(),
    stopCondition
)

class SyncObservable<out TScrap : Scrap, out TRef : RefItem, TPageRef : RefPage>(
    private val pageProvider: RefPageProvider<TPageRef>,
    private val itemProvider: ScrapCollectionParser<TPageRef, TRef>,
    private val scrapParser: ScrapParser<TRef, TScrap>,
    private val shouldStop: ((TRef) -> Boolean) = { false },
    private val maxActive: Int = 100
) : AdSource<TScrap> {

    suspend override fun subscribe(onNext: suspend (TScrap) -> Unit, onError: (Throwable) -> Unit) {

        val refProducer = itemRefProducer()

        val jobs = mutableListOf<Job>()

        while (!refProducer.isClosedForReceive) {
            val ref = refProducer.receive()

            jobs.removeIf { it.isCompleted || it.isCancelled }
            if (jobs.count() > maxActive) {
                logger.info { "current active job count=${jobs.count()}, delaying" }
                delay(100)
            }

            jobs += launch(CommonPool) {
                val scrap: Try<TScrap> = scrapParser.retryable().parse(ref)
                when (scrap) {
                    is Try.Success -> onNext(scrap.result)
                    is Try.Error -> onError(scrap.error)
                }
            }
            if (shouldStop(ref)) refProducer.cancel()
        }

        runBlocking {
            jobs.filter { it.isActive }.forEach { it.join() }
        }
    }

    fun itemRefProducer() = produce(capacity = 50) {
        buildSequence {
            (0..Int.MAX_VALUE).forEach { page ->
                yield(pageProvider.get(page))
            }
        }.forEach { pageRef ->
            val refs = itemProvider.retryable().parse(pageRef)
            refs.fold { throw RuntimeException() }
                ?.forEach { ref ->
                    send(ref)
                }
        }
    }
}

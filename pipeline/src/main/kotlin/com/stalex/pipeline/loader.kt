package com.stalex.pipeline

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.NonCancellable.cancel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import mu.KotlinLogging
import kotlin.coroutines.experimental.buildSequence

val logger = KotlinLogging.logger {}

interface Scrap

interface PageRef
class PageRefImpl(val url: String) : PageRef

interface RefItem
data class RefItemImpl(val id: String) : RefItem

interface Parser<in Source, out Target> {
    suspend fun parse(page: Source): Target
}

interface ScrapCollectionParser<in T : PageRef, out R : RefItem> : Parser<T, List<R>>
interface ScrapParser<in R : Any?, out T : Scrap> : Parser<R, T>

interface RefPageProvider<out T : PageRef> {
    fun get(index: Int): T
}

interface LoaderFactory<out S : Scrap, P : PageRef, R : RefItem> {
    fun pageProvider(): RefPageProvider<P>
    fun itemProvider(): ScrapCollectionParser<P, R>
    fun parser(): ScrapParser<R, S>
}

fun <S : Scrap, P : PageRef, TRef : RefItem> LoaderFactory<S, P, TRef>.createSyncObservable(
    stopCondition: ((TRef) -> Boolean) = { false }
): SyncObservable<S, TRef> {
    val refProducer: ReceiveChannel<TRef> =
        itemRefProducer(
            pageProvider(),
            itemProvider(),
            stopCondition
        )
    return SyncObservable(refProducer, parser())
}

fun <TRef : RefItem, TPageRef : PageRef> itemRefProducer(
    pageProvider: RefPageProvider<TPageRef>,
    itemProvider: ScrapCollectionParser<TPageRef, TRef>,
    shouldStop: ((TRef) -> Boolean) = { false }
): ReceiveChannel<TRef> = produce(capacity = 50) {
    buildSequence {
        (0..Int.MAX_VALUE).forEach { page ->
            yield(pageProvider.get(page))
        }
    }.forEach { pageRef ->
        val refs = itemProvider.retry().parse(pageRef)
        refs.fold { throw RuntimeException() }
            ?.forEach { ref ->
                if (shouldStop(ref)) {
                    cancel()
                } else {
                    send(ref)
                }
            }
    }
}

class SyncObservable<out TScrap : Scrap, out TRef : RefItem>(
    private val refProducer: ReceiveChannel<TRef>,
    private val scrapParser: ScrapParser<TRef, TScrap>,
    private val maxActive: Int = 100
) : ObservableSource<TScrap> {

    override suspend fun subscribe(onNext: suspend (TScrap) -> Unit, onError: (Throwable) -> Unit) {

        val jobs = mutableListOf<Job>()

        while (!refProducer.isClosedForReceive) {
            val ref = refProducer.receiveOrNull()
            if (ref != null) {
                jobs += launch(CommonPool) {
                    val scrap: Try<TScrap> = scrapParser.retry().parse(ref)
                    when (scrap) {
                        is Try.Success -> onNext(scrap.result)
                        is Try.Error -> onError(scrap.error)
                    }
                }
            }
        }

        runBlocking {
            jobs.filter { !it.isCompleted && !it.isCancelled }.forEach { it.join() }
        }
    }
}

class DirectSyncObservable<out TScrap : Scrap>(
    private val scrapParser: ScrapParser<Any?, TScrap>,
    private val shouldStop: (() -> Boolean) = { false },
    private val maxActive: Int = 100
) : ObservableSource<TScrap> {

    override suspend fun subscribe(onNext: suspend (TScrap) -> Unit, onError: (Throwable) -> Unit) {

        val jobs = mutableListOf<Job>()

        while (!shouldStop()) {

            jobs.removeIf { it.isCompleted || it.isCancelled }
            if (jobs.count() > maxActive) {
                logger.info { "current active job count=${jobs.count()}, delaying" }
                delay(100)
            }

            jobs += launch(CommonPool) {
                val scrap: Try<TScrap> = scrapParser.retry().parse(null)
                when (scrap) {
                    is Try.Success -> onNext(scrap.result)
                    is Try.Error -> onError(scrap.error)
                }
            }
        }

        runBlocking {
            jobs.filter { it.isActive }.forEach { it.join() }
        }
    }
}
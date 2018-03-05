package com.stalex.pipeline

import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.produce
import mu.KotlinLogging
import kotlin.coroutines.experimental.buildSequence

val logger = KotlinLogging.logger {}

interface Scrap

interface RefPage
class RefPageImpl(val url: String) : RefPage

interface RefItem
data class RefItemImpl(val id: String) : RefItem

interface Parser<in Source, out Target> {
    fun parse(page: Source): Target
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
    stopCondition: ((Scrap) -> Boolean)?
) = SyncObservable(
    pageProvider(),
    itemProvider(),
    loader(),
    stopCondition
)

class SyncObservable<out S : Scrap, P : RefPage, R : RefItem>(
    private val pageProvider: RefPageProvider<P>,
    private val itemProvider: ScrapCollectionParser<P, R>,
    private val scrapParser: ScrapParser<R, S>,
    private val shouldStop: ((Scrap) -> Boolean)? = null
) : AdSource<S> {

    suspend override fun subscribe(onNext: suspend (S) -> Unit) {
        itemProducer().consumeEach {
            onNext(it)
        }
    }

    fun itemProducer() = produce {
        val pageSequence = produce {
            (0 until 10).forEach { page ->
                val element = pageProvider.get(page)
                logger.info { "for page $page recieve $element" }
                send(element)

//                delay(1000)
            }
        }

        pageSequence.consumeEach { page ->
            val scrapCollection = itemProvider.parse(page)
            logger.info { "scrapCollection$scrapCollection" }
            scrapCollection.forEach { item ->
                logger.info { }
                val scrap = scrapParser.parse(item)
//                if (shouldStop?.invoke(scrap) == true) {
//                    return@forEach
//                } else {
                    send(scrap)

//                    delay(2000)
//                }
            }
        }
    }

    fun itemSeq(): Sequence<S> = buildSequence {

        val pageSequence: Sequence<P> = buildSequence {
            (0..100).forEach { page ->
                yield(pageProvider.get(page))
            }
        }

        pageSequence.iterator().forEach { page ->
            itemProvider.parse(page).forEach { item ->
                yield(scrapParser.parse(item))
            }
        }
    }
}

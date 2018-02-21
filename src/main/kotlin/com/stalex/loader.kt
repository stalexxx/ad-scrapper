package com.stalex

import kotlin.coroutines.experimental.buildSequence

interface SourceItem

interface SourceMart

interface RefPage
class RefPageImpl(val url: String) : RefPage

interface RefItem
data class RefItemImpl(val id: String) : RefItem

interface RefItemProvider<in T : RefPage, out R : RefItem> {
    fun get(page: T): List<R>
}

interface RefPageProvider<out T : RefPage> {
    fun get(index: Int): T
}

interface EndItemProvider<in R : RefItem, out T : SourceItem> {
    fun load(ref: R): T
}

fun <S : SourceItem, P : RefPage, R : RefItem> createSyncObservable(
    pageProviderFactory: () -> RefPageProvider<P>,
    itemProviderFactory: () -> RefItemProvider<P, R>,
    loaderFactory: () -> EndItemProvider<R, S>,
    stopCondition: ((SourceItem) -> Boolean)? = null
) = SyncObservable(
    pageProviderFactory(),
    itemProviderFactory(),
    loaderFactory(),
    stopCondition
)

class SyncObservable<out S : SourceItem, P : RefPage, R : RefItem>(
    private val pageProvider: RefPageProvider<P>,
    private val itemProvider: RefItemProvider<P, R>,
    private val loader: EndItemProvider<R, S>,
    private val stopCondition: ((SourceItem) -> Boolean)? = null
) : AdSource<S> {

    override fun subscribe(onNext: (S) -> Unit) {
        itemSeq().iterator().let {
            while (it.hasNext()) {
                val item = it.next()
                onNext(item)
                if (stopCondition?.invoke(item)?.not() == true) {
                    return
                }
            }
        }
    }

    fun itemSeq(): Sequence<S> = buildSequence {

        val pageSequence: Sequence<P> = buildSequence {
            (0..100).forEach { page ->
                val refPage: P = pageProvider.get(page)
                yield(refPage)
            }
        }

        pageSequence.iterator().forEach { page ->
            itemProvider.get(page).forEach { item ->
                println("loading in ${Thread.currentThread()}")
                yield(loader.load(item))
            }
        }
    }
}

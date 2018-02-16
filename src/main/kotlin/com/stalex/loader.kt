package com.stalex

import kotlin.coroutines.experimental.buildSequence

interface SourceItem

interface SourceMart

interface RefPage
class RefPageImpl : RefPage

interface RefItem
data class RefItemImpl(val id: String) : RefItem

typealias RefItemProvider = (RefPage) -> List<RefItem>
typealias RefPageProvider = (Int) -> RefPage

interface EndItemLoader<out T : SourceItem> {
    fun load(ref: RefItem) : T
}

class SyncObservable <out S: SourceItem> (
    private val pageProvider: RefPageProvider,
    private val itemProvider: RefItemProvider,
    private val loader: EndItemLoader<S>,
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

        val pageSequence: Sequence<RefPage> = buildSequence {
            (0..100).forEach { page ->
                val refPage: RefPage = pageProvider(page)
                yield(refPage)
            }
        }

        pageSequence.iterator().forEach { page ->
            itemProvider(page).forEach { item ->
                println("loading in ${Thread.currentThread()}")
                yield(loader.load(item))
            }
        }
    }
}

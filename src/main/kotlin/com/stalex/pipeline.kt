package com.stalex

import io.reactivex.Observable
import io.reactivex.Observer
import kotlin.coroutines.experimental.buildSequence

typealias PipelineChain<E> = MutableList<PipelineLink<E>>

class DefaultPipeline<E : EmitItem>(
    private val chain: PipelineChain<E> = mutableListOf()
) : Pipeline<DefaultPipeline<E>, E> {

    var source: AdSource<E>? = null

    override fun withSource(source: AdSource<E>): DefaultPipeline<E> {
        this.source = source
        return this
    }

    override fun with(link: PipelineLink<E>): DefaultPipeline<E> {
        this.chain += link
        return this
    }

    override fun start() {
        source?.subscribe { e ->
            this.chain
                .stream()
                .parallel()
                .forEach { it.handle(e) }
        }
    }
}

interface AdSource<out E : EmitItem> {
    fun subscribe(handler: (E) -> Unit)
}

interface Pipeline<T : Pipeline<T, E>, E : EmitItem> {
    fun withSource(source: AdSource<E>): T
    fun with(link: PipelineLink<E>): T
    fun start()
}


interface PipelineLink<E> {
    fun handle(e: E)
}

interface EmitItem

class AvitoItem : EmitItem {
    var id: Int = 0
}

interface SourceMart

interface RefPage
class RefPageImpl : RefPage

interface RefItem
class RefItemImpl : RefItem

typealias SourceItem = AvitoItem
typealias RefItemProvider = (RefPage) -> List<RefItem>
typealias RefPageProvider = (Int) -> RefPage

typealias EndItemLoader = (RefItem) -> SourceItem

fun itemSeq(
    pageProvider: RefPageProvider,
    itemProvider: RefItemProvider,
    loader: EndItemLoader

): Sequence<SourceItem> = buildSequence {

    val pageSequence: Sequence<RefPage> = buildSequence {
        (0..100).forEach { page ->
            val refPage: RefPage = pageProvider(page)
            yield(refPage)
        }
    }

    pageSequence.iterator().forEach { page ->
        itemProvider(page).forEach { item ->
            yield(loader(item))
        }
    }
}

typealias StopPredicate = (AvitoItem?) -> Boolean

class AvitoObservable : Observable<AvitoItem>() {


    private var sell = 100000L


    override fun subscribeActual(p0: Observer<in AvitoItem>?) {
//        itemSeq()
    }

}

class ObservableSource(val observable: Observable<AvitoItem>) : AdSource<AvitoItem> {
    override fun subscribe(handler: (AvitoItem) -> Unit) {
        observable.subscribe(handler)
    }

}

interface AdStorer<T : EmitItem> : PipelineLink<T>
interface AdLogger<T : EmitItem> : PipelineLink<T>

class MemoryStorer : AdStorer<AvitoItem> {
    override fun handle(e: AvitoItem) {
        print("stored $e")
    }
}

class ConsoleAdLogger : AdLogger<AvitoItem> {
    override fun handle(e: AvitoItem) {
        print("stored $e")
    }

}


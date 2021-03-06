package com.stalex.pipeline

typealias PipelineChain<E> = MutableList<PipelineLink<E>>

class DefaultPipeline<E : Scrap>(
    private val chain: PipelineChain<E> = mutableListOf()
) : Pipeline<DefaultPipeline<E>, E> {

    var mySource: ObservableSource<E>? = null

    override fun withSource(source: ObservableSource<E>): DefaultPipeline<E> {
        this.mySource = source
        return this
    }

    override fun with(link: PipelineLink<E>): DefaultPipeline<E> {
        this.chain += link
        return this
    }

    override suspend fun start() {
        mySource?.subscribe ({ e ->
            for (it in this.chain) {
                it.handle(e)
            }
        }, {
            TODO()
        })
    }
}

interface ObservableSource<out E : Scrap> {
    suspend fun subscribe(onNext: suspend (E) -> Unit, onError: (Throwable) -> Unit = {})
}

interface Pipeline<T : Pipeline<T, E>, E : Scrap> {
    fun withSource(source: ObservableSource<E>): T
    fun with(link: PipelineLink<E>): T
    suspend fun start()
}

interface PipelineLink<E> {
    suspend fun handle(e: E)
}

interface AdStorer<T : Scrap> : PipelineLink<T>
interface AdLogger<T : Scrap> : PipelineLink<T>
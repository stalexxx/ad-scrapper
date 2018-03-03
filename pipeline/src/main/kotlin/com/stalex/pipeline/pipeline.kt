package com.stalex.pipeline

typealias PipelineChain<E> = MutableList<PipelineLink<E>>

class DefaultPipeline<E : SourceItem>(
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

    suspend override fun start() {
        source?.subscribe { e ->
            for (it in this.chain) {
                it.handle(e)
            }
        }
    }
}

interface AdSource<out E : SourceItem> {
    suspend fun subscribe(handler: suspend (E) -> Unit)
}

interface Pipeline<T : Pipeline<T, E>, E : SourceItem> {
    fun withSource(source: AdSource<E>): T
    fun with(link: PipelineLink<E>): T
    suspend fun start()
}

interface PipelineLink<E> {
    suspend fun handle(e: E)
}

interface AdStorer<T : SourceItem> : PipelineLink<T>
interface AdLogger<T : SourceItem> : PipelineLink<T>
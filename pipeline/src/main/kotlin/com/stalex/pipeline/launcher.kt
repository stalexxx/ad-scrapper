package com.stalex.pipeline

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import mu.KotlinLogging

fun <T : Scrap> launchPipeline(observableSource: ObservableSource<T>, init: DefaultPipeline<T>.() -> Unit): Job {
    val logger = KotlinLogging.logger {}
    return launch(CommonPool) {
        logger.info("thread: ${Thread.currentThread().name}")
        DefaultPipeline<T>()
            .withSource(observableSource)
            .apply(init)
            .start()
    }
}
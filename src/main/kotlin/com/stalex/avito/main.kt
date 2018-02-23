package com.stalex.avito

import com.stalex.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import mu.KotlinLogging
import nolambda.skrape.SkrapeLogger

private val logger = KotlinLogging.logger {}

data class AvitoRefItem(var link: String, val text: String = "") : RefItem

data class AvitoSourceItem(
    val url: String,
    val description: String? = null,
    val title: String? = null,
    val address: String? = null,
    val params: Map<String, String>? = null,
    val advParams: Map<String, List<String>>? = null,
    val metro: List<String>? = null,
    val user: AvitoUser? = null,
    val isAgent: Boolean? = null,
    val price: String? = null,
    val subPrice: String? = null

) : SourceItem

data class AvitoUser(val link: String, val name: String)

fun avitoSyncFactory() =
    createSyncObservable(
        { AvitoPageProvider() },
        { AvitoSourceItemProvider() },
        { AvitoEndItemProvider() },
        { true }//todo write exist checker
    )

fun main(args: Array<String>) = runBlocking {
    SkrapeLogger.enableLog = false
    
    val job = launchPipeline()
    
    job.join()
}

fun launchPipeline(): Job {
    val logger = KotlinLogging.logger {}
    val x by lazy {  }
    return launch(CommonPool) {
        SkrapeLogger.enableLog = false
        logger.info("thread: ${Thread.currentThread().name}")
        DefaultPipeline<AvitoSourceItem>()
            .withSource(
                avitoSyncFactory()
            )
            .with(MongoStorer())
            .with(ConsoleAdLogger())
            .start()
    }
}


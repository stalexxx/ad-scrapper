package com.stalex.avito

import com.stalex.ConsoleAdLogger
import com.stalex.DefaultPipeline
import com.stalex.EndItemProvider
import com.stalex.LoaderFactory
import com.stalex.MongoStorer
import com.stalex.PipelineLink
import com.stalex.RefItem
import com.stalex.RefItemProvider
import com.stalex.RefPageImpl
import com.stalex.RefPageProvider
import com.stalex.SourceItem
import com.stalex.createSyncObservable
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
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

class AvitoFactory : LoaderFactory<AvitoSourceItem, RefPageImpl, AvitoRefItem> {
    override fun pageProvider(): RefPageProvider<RefPageImpl> = AvitoPageProvider()
    override fun itemProvider(): RefItemProvider<RefPageImpl, AvitoRefItem> = AvitoSourceItemProvider()
    override fun loader(): EndItemProvider<AvitoRefItem, AvitoSourceItem> = AvitoEndItemProvider()
}

fun avitoSyncFactory() =
    AvitoFactory().createSyncObservable {
        true
    }

fun main(args: Array<String>) = runBlocking {
    SkrapeLogger.enableLog = false
    val job = launchPipeline()
    job.join()
}

fun launchPipeline(): Job {
    val logger = KotlinLogging.logger {}
    return launch(CommonPool) {
        SkrapeLogger.enableLog = false
        logger.info("thread: ${Thread.currentThread().name}")
        DefaultPipeline<AvitoSourceItem>()
            .withSource(
                avitoSyncFactory()
            )
            .with(MongoStorer())
            .with(ConsoleAdLogger())
            .with(AvitoDelay())
            .start()
    }
}

class AvitoDelay : PipelineLink<AvitoSourceItem> {
    suspend override fun handle(e: AvitoSourceItem) {
        delay(5000)
    }
}

package com.stalex.avito

import com.stalex.pipeline.AdLogger
import com.stalex.pipeline.AdStorer
import com.stalex.pipeline.DefaultPipeline
import com.stalex.pipeline.EndItemProvider
import com.stalex.pipeline.LoaderFactory
import com.stalex.pipeline.PipelineLink
import com.stalex.pipeline.RefItem
import com.stalex.pipeline.RefItemProvider
import com.stalex.pipeline.RefPageImpl
import com.stalex.pipeline.RefPageProvider
import com.stalex.pipeline.SourceItem
import com.stalex.pipeline.createSyncObservable
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

class MongoStorer : AdStorer<AvitoSourceItem> {

    companion object {
//        val client = KMongo.createClient()
//        val database = client.getDatabase("avito")!!
//        val collection = database.getCollection<AvitoSourceItem>()
    }

    suspend override fun handle(e: AvitoSourceItem) {
//        collection.insertOne(e) todo move to another module
    }
}

class ConsoleAdLogger : AdLogger<AvitoSourceItem> {
    private val logger = KotlinLogging.logger {}

    suspend override fun handle(e: AvitoSourceItem) {
        logger.info {
            e
        }
    }
}

package com.stalex.avito

import com.github.salomonbrys.kodein.instance
import com.stalex.pipeline.AdLogger
import com.stalex.pipeline.AdStorer
import com.stalex.pipeline.DefaultPipeline
import com.stalex.pipeline.LoaderFactory
import com.stalex.pipeline.PipelineLink
import com.stalex.pipeline.RefItem
import com.stalex.pipeline.RefPageImpl
import com.stalex.pipeline.RefPageProvider
import com.stalex.pipeline.Scrap
import com.stalex.pipeline.ScrapCollectionParser
import com.stalex.pipeline.ScrapParser
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

data class AvitoScrap(
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

) : Scrap

data class AvitoUser(val link: String, val name: String)

class AvitoFactory : LoaderFactory<AvitoScrap, RefPageImpl, AvitoRefItem> {
    override fun pageProvider(): RefPageProvider<RefPageImpl> = AvitoPageProvider()
    override fun itemProvider(): ScrapCollectionParser<RefPageImpl, AvitoRefItem> = AvitoSourceItemProvider(kodein.instance())
    override fun loader(): ScrapParser<AvitoRefItem, AvitoScrap> = AvitoScrapParser(kodein.instance())
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
        DefaultPipeline<AvitoScrap>()
            .withSource(
                avitoSyncFactory()
            )
            .with(MongoStorer())
            .with(ConsoleAdLogger())
            .with(AvitoDelay())
            .start()
    }
}

class AvitoDelay : PipelineLink<AvitoScrap> {
    suspend override fun handle(e: AvitoScrap) {
        delay(5000)
    }
}

class MongoStorer : AdStorer<AvitoScrap> {

    companion object {
//        val client = KMongo.createClient()
//        val database = client.getDatabase("avito")!!
//        val collection = database.getCollection<AvitoSourceItem>()
    }

    suspend override fun handle(e: AvitoScrap) {
//        collection.insertOne(e) todo move to another module
    }
}

class ConsoleAdLogger : AdLogger<AvitoScrap> {
    private val logger = KotlinLogging.logger {}

    suspend override fun handle(e: AvitoScrap) {
        logger.info {
            e
        }
    }
}

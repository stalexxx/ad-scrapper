package com.stalex.server

import com.stalex.avito.AvitoScrap
import com.stalex.avito.avitoSyncFactory
import com.stalex.pipeline.AdStorer
import com.stalex.pipeline.launchPipeline
import kotlinx.coroutines.experimental.runBlocking
import nolambda.skrape.SkrapeLogger

fun launchAvito() = runBlocking {
    SkrapeLogger.enableLog = false
    val job = launchPipeline(avitoSyncFactory()) {
        this.with(MongoStorer())
            .with(ConsoleAdLogger())
    }

    job.join()
}

class MongoStorer : AdStorer<AvitoScrap> {
    companion object {
//        val client = KMongo.createClient()
//        val database = client.getDatabase("avito")!!
//        val collection = database.getCollection<AvitoSourceItem>()
    }

    override suspend fun handle(e: AvitoScrap) {
//        collection.insertOne(e) todo move to another module
    }
}

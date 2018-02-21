package com.stalex.avito

import com.stalex.*
import nolambda.skrape.SkrapeLogger

data class AvitoRefItem(var link: String, val text:String = "") : RefItem

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

fun avitoSyncFactory() = createSyncObservable(
    { AvitoPageProvider() },
    { AvitoSourceItemProvider() },
    { AvitoEndItemProvider() },
    { true }//todo write exist checker
)

fun main(args: Array<String>) {


    SkrapeLogger.enableLog = false

    val pipeline = DefaultPipeline<AvitoSourceItem>()
        .withSource(
            avitoSyncFactory()
        )
        .with(MongoStorer())
        .with(ConsoleAdLogger())
        .start()


}
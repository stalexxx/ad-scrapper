package com.stalex.avito

import com.beust.klaxon.Klaxon
import com.stalex.RefItem
import com.stalex.RefItemProvider
import com.stalex.RefPage
import nolambda.skrape.Skrape
import nolambda.skrape.nodes.*
import nolambda.skrape.processor.jsoup.JsoupDocumentParser

class AvitoItemProvider(private val path: String) : RefItemProvider {
    override fun invoke(p1: RefPage): List<RefItem> {
//https://www.avito.ru/sankt-peterburg/nedvizhimost

        val results = Page(path) {

            "items" to query("a.item-description-title-link") {
                "link" to attr("href")
                "text" to text()
            }
        }.run {
            Skrape(JsoupDocumentParser()).request(this)
        }.let {
                Klaxon().parse<LoadItemResult>(it)
            }
        return results?.items ?: ParsingFail()
    }


    data class LoadItem(
        val link: String,
        val text: String
    ) : RefItem

    data class LoadItemResult(
        val items: List<LoadItem>
    )
}

fun ParsingFail(): Nothing = throw AdScrapperException.ParseException()

sealed class AdScrapperException : RuntimeException() {
    class ParseException() : AdScrapperException()

}


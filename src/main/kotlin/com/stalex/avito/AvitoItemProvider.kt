package com.stalex.avito

import com.google.gson.GsonBuilder
import com.stalex.RefItemProvider
import com.stalex.RefPageImpl
import nolambda.skrape.Skrape
import nolambda.skrape.nodes.*
import nolambda.skrape.processor.jsoup.JsoupDocumentParser

class AvitoSourceItemProvider : RefItemProvider<RefPageImpl, AvitoRefItem> {
    override fun get(page: RefPageImpl): List<AvitoRefItem> {
        
        return Page(page.url) {
            "items" to query("a.item-description-title-link") {
                "link" to attr("href")
                "text" to text()
            }
        }.run { Skrape(JsoupDocumentParser()).request(this)
        }.run { GsonBuilder().create().fromJson(this, LoadItemResult::class.java).items }
            .onEach { it.link = "https://www.avito.ru${it.link}" }
    }
    
    
    data class LoadItemResult(
        val items: List<AvitoRefItem>
    )
}


fun ParsingFail(): Nothing = throw AdScrapperException.ParseException()

sealed class AdScrapperException : RuntimeException() {
    class ParseException() : AdScrapperException()
    
}


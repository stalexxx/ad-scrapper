package com.stalex.avito

import com.stalex.pipeline.PageRefImpl
import com.stalex.pipeline.ScrapCollectionParser
import nolambda.skrape.Skrape
import nolambda.skrape.nodes.attr
import nolambda.skrape.nodes.query
import nolambda.skrape.nodes.text
import nolambda.skrape.nodes.to

class AvitoSourceItemProvider(var skrape: Skrape<String>) : ScrapCollectionParser<PageRefImpl, AvitoRefItem> {

    override suspend fun parse(page: PageRefImpl): List<AvitoRefItem> {

        val result = skrape.requestPage<LoadItemResult>(page.url) {
            "items" to query("a.item-description-title-link") {
                "link" to attr("href")
                "text" to text()
            }
        }

        return result.items.onEach { it.link = "https://www.avito.ru${it.link}" }
    }

    data class LoadItemResult(
        val items: List<AvitoRefItem>
    )
}

package com.stalex.avito

import com.stalex.pipeline.ScrapParser
import nolambda.skrape.Skrape
import nolambda.skrape.nodes.attr
import nolambda.skrape.nodes.container
import nolambda.skrape.nodes.query
import nolambda.skrape.nodes.text
import nolambda.skrape.nodes.to

class AvitoScrapParser(var skrape: Skrape<String>) : ScrapParser<AvitoRefItem, AvitoScrap> {

    suspend override fun parse(ref: AvitoRefItem): AvitoScrap {

        val (title, address, description, params, advParams, user, price, subPrice, metro) =
            skrape.requestPage<Results>(ref.link) {
                "item" to container {
                    "title" to query("span.title-info-title-text") { "text" to text() }
                    "description" to query("div.item-description-text p") { "text" to text() }
                    "price" to query("span.price-value-string") { "text" to text() }
                    "subPrice" to query("div.item-price-sub-price") { "text" to text() }
                    "address" to query("span.item-map-address") { "text" to text() }
                    "metro" to query("span.item-map-metro") { "text" to text() }

                    "params" to query("li.item-params-proxyList-item") {

                        "type" to query("span.item-params-label") {
                            "text" to text()
                        }

                        "entry" to text()
                    }

                    "advParams" to query("li.advanced-params-param") {
                        //                    "entry" to text()

                        "type" to query("div.advanced-params-param-title") {
                            "text" to text()
                        }
                        "value" to query("li.advanced-params-param-item") {
                            "text" to text()
                        }
                    }

                    "user" to query("div.seller-info-name a") {
                        "link" to attr("href")
                        "name" to text()
                    }
                }
            }.item

        return AvitoScrap(
            url = ref.link,
            description = description.text,
            title = title.text,
            address = address.text,
            params = params.map { it.pair }.toTypedArray().toMap(),
            advParams = advParams.map { it.type.text to it.value.map { it.text } }.toMap(),
            isAgent = false,
            user = user.first(),
            price = price.text,
            subPrice = subPrice.text,
            metro = metro.map { it.text }
        )
    }
}

data class ResultItem(
    var title: SingleTextList,
    var address: SingleTextList,
    var description: SingleTextList,
    var params: List<Param>,
    val advParams: List<AdvParam>,
    val user: List<AvitoUser>,
    val price: SingleTextList,
    val subPrice: SingleTextList,
    val metro: SingleTextList
)

data class Results(var item: ResultItem)

data class Param(
    private var entry: String,
    var type: SingleTextList

) {
    val pair
        get() = type.text.replace(":", "") to entry.substringAfter(type.text)
}

data class AdvParam(var type: SingleTextList,
                    var value: SingleTextList)

data class HasText(var text: String)

class SingleTextList : ArrayList<HasText>() {
    val text
        get() = firstOrNull()?.text ?: ""
}
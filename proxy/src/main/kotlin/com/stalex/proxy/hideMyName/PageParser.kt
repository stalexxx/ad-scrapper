package com.stalex.proxy.hideMyName

import com.stalex.pipeline.RefItem
import com.stalex.pipeline.PageRefImpl
import com.stalex.pipeline.Scrap
import com.stalex.pipeline.ScrapCollectionParser
import com.stalex.proxy.requestPage
import nolambda.skrape.Skrape
import nolambda.skrape.nodes.query
import nolambda.skrape.nodes.text
import nolambda.skrape.nodes.to
import java.net.Proxy

class ProxyInfo(
    val host: String,
    val port: Int,
    val protocol: Proxy.Type = Proxy.Type.DIRECT,
    val location: String? = null
) : RefItem, Scrap {
    companion object {
        fun parseProxyType(type: String): Proxy.Type =
            when (type.toLowerCase()) {
                "http", "https" -> java.net.Proxy.Type.HTTP
                "socks5", "socks4" -> java.net.Proxy.Type.SOCKS
                else -> {
                    java.net.Proxy.Type.DIRECT
                }
            }
    }
}

class PageParser(var skrape: Skrape<String>) : ScrapCollectionParser<PageRefImpl, ProxyInfo> {
    override suspend fun parse(page: PageRefImpl): List<ProxyInfo> {
        val result = skrape.requestPage<LoadItemResult>(page.url) {
            "items" to query("table.proxy__t tbody tr") {
                "tds" to query("td") {
                    "text" to text()
                }
            }
        }

        return result.items.map {
            ProxyInfo(host = it.tds[0].text,
                port = it.tds[1].text.toInt(),
                protocol = ProxyInfo.parseProxyType(it.tds[4].text.split(",").first()),
                location = it.tds[2].text
            )
        }
    }
}

data class Td(var text: String)

data class Tr(var tds: List<Td>)

data class LoadItemResult(
    val items: List<Tr>
)
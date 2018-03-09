package com.stalex.proxy.hideMyName

import com.stalex.pipeline.PageRefImpl
import com.stalex.pipeline.RefItem
import com.stalex.pipeline.RefPageProvider
import com.stalex.pipeline.ScrapParser

internal class Provider : RefPageProvider<PageRefImpl> {
    override fun get(index: Int): PageRefImpl {
        return PageRefImpl("https://hidemy.name/ru/proxy-list/?start=${index * 64}#list")
    }
}

fun <T : RefItem> identity() = object : ScrapParser<ProxyInfo, ProxyInfo> {
    override suspend fun parse(page: ProxyInfo): ProxyInfo = page
}

//fun proxyObservable() = SyncObservable(
//    Provider(),
//    PageParser(kodein.instance()),
//    identity<RefItem>()
//
//)

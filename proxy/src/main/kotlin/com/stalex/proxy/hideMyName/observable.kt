package com.stalex.proxy.hideMyName

import com.github.salomonbrys.kodein.instance
import com.stalex.pipeline.RefItem
import com.stalex.pipeline.RefPageImpl
import com.stalex.pipeline.RefPageProvider
import com.stalex.pipeline.ScrapParser
import com.stalex.pipeline.SyncObservable
import com.stalex.proxy.kodein

internal class Provider : RefPageProvider<RefPageImpl> {
    override fun get(index: Int): RefPageImpl {
        return RefPageImpl("https://hidemy.name/ru/proxy-list/?start=${index * 64}#list")
    }
}

fun <T : RefItem> identity() = object : ScrapParser<ProxyInfo, ProxyInfo> {
    override suspend fun parse(page: ProxyInfo): ProxyInfo = page
}

fun proxyObservable() = SyncObservable(
    Provider(),
    PageParser(kodein.instance()),
    identity<RefItem>()

)

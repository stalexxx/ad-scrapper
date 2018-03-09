package com.stalex.server

import com.stalex.pipeline.AdStorer
import com.stalex.pipeline.launchPipeline
import com.stalex.proxy.hideMyName.ProxyInfo
import com.stalex.proxy.hideMyName.proxyObservable
import kotlinx.coroutines.experimental.runBlocking

fun launchProxy() = runBlocking {
    //    SkrapeLogger.enableLog = false

    val job = launchPipeline(proxyObservable()) {
        this.with(object : AdStorer<ProxyInfo> {
            override suspend fun handle(e: ProxyInfo) {
            }
        })
    }

    job.join()
}
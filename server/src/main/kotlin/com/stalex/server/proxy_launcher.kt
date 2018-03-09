package com.stalex.server

//fun launchProxy() = runBlocking {
//    //    SkrapeLogger.enableLog = false
//
//    val job = launchPipeline(proxyObservable()) {
//        this.with(object : AdStorer<ProxyInfo> {
//            override suspend fun handle(e: ProxyInfo) {
//            }
//        })
//    }
//
//    job.join()
//}
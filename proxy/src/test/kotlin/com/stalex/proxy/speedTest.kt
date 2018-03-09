package com.stalex.proxy

//fun main(args: Array<String>) {
//    val iterator = proxySequence().iterator()
//    val job = launch(CommonPool) {
//
//        var proxyInfo: ProxyInfo? = null
//        measureTimeMillis {
//            proxyInfo = iterator.next()
//        }.also {
//            println("!!!! proxy got in $it ms")
//        }
//
//
//        if (proxyInfo != null) {
//            measureTimeMillis {
//
//                Page("http://ya.ru") {
//                    "items" to query("div.search2__button button.button") {
//                        "text" to text()
//                    }
//                }.run {
//                    Skrape(JsoupDocumentParser(proxyInfo!!.toProxy())).request(this)
//                }.also { json ->
//                        json should { "Найти" in it }
//
//                    }
//
//            }.also {
//                println("!!!! page got in $it ms, speed: ${proxyInfo!!.speed} ")
//
//            }
//        }
//
//    }
//    job.join()
//
//}
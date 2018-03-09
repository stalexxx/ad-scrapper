package com.stalex.proxy

//-Djavax.net.debug=ssl:record
//class ProxyTest : StringSpec() {
//    init {
//        SkrapeLogger.enableLog = false
//
//        "test single" {
//            val start = System.currentTimeMillis()
//
//            val jobs = mutableListOf<Job>()
//            for (i in (0..50)) {
//
//                val job = launch {
//                    println("starting job $i")
//                    delay((10000 * Random().nextDouble()).toInt() + 500)
////                    var proxyInfo: ProxyInfo? = null
////                    measureTimeMillis {
////                        proxyInfo = retrieveProxy() //retrieveProxy()
////                    }.also {
////                        println("!!!! proxy got in $it ms")
////                    }
//
////                    if (proxyInfo != null) {
//                        kotlin.system.measureTimeMillis {
//                            val proxy = proxyList.rand()
//                            try {
//
//                                val result = doSyncRequest(proxy)
//                                result should { "Найти" in it }
//                            } catch (se: Exception) {
//                                println("ex: ${se.localizedMessage} proxy: $proxy")
//                            }
//
//
////                        }.also { println("!!!! page got in $it ms, speed: ${proxyInfo!!.speed} ") }
//                        }.also { println("!!!! page got in $it ms") }
////                    }
//
//                }
//                jobs.add(job)
////                    job.join()
//            }
//
//            runBlocking {
//                for (job in jobs) {
//                    if (!job.isCompleted) {
//                        job.join()
//                    }
//                }
//
////                delay(100000)
//            }
//            println("!!total time ${System.currentTimeMillis() - start}ms ")
//        }
//
//
//    }
//
//    private fun doSyncRequest(proxyInfo: Proxy?): String {
//        return Page("http://ya.ru") {
//            "items" to query("div.search2__button button.button") {
//                "text" to text()
//            }
//        }.run {
//            Skrape(JsoupDocumentParser(proxyInfo)).request(this)
//        }
//    }
//
//}
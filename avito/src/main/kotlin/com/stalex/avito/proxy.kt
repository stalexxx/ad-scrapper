//package com.stalex.avito
//
//import com.github.kittinunf.fuel.Fuel
//import com.github.kittinunf.fuel.core.FuelManager
//import com.github.kittinunf.fuel.core.Request
//import com.github.kittinunf.result.Result
//import com.github.salomonbrys.kotson.registerTypeAdapter
//import com.google.gson.GsonBuilder
//import kotlinx.coroutines.experimental.runBlocking
//import java.net.InetSocketAddress
//import java.net.Proxy
//import java.util.*
//import kotlin.coroutines.experimental.suspendCoroutine
//
//
//data class ProxyInfo(
//    val supportsHttps: Boolean,
//    val protocol: Proxy.Type?,
//    val ip: String,
//    val port: Int,
//    val get: Boolean,
//    val post: Boolean,
//    val cookies: Boolean,
//    val referer: Boolean,
////    val user-agent: Boolean,
//    val anonymityLevel: Int,
////val websites
////val example    true
////val google    false
////val amazon    true
////val country    "BR"
////val tsChecked    1519414682
////val curl    "socks5://179.105.195.59:64731"
////val ipPort    "179.105.195.59:64731"
//    val type: String,
//    val speed: Double
////    val otherProtocols {}
//
//) {
//    companion object {
//        val gson = GsonBuilder().registerTypeAdapter<Proxy.Type> {
//            this.deserialize {
//                val json = it.json
//                when (json.asString.toLowerCase()) {
//                    "http" -> java.net.Proxy.Type.HTTP
//                    "socks5", "socks4" -> java.net.Proxy.Type.SOCKS
//                    else -> {
//                        java.net.Proxy.Type.HTTP
//                    }
//                }
//            }
//        }.create()
//
//        fun fromString(input: String) = gson.fromJson<ProxyInfo>(input, ProxyInfo::class.java)
//    }
//
//    fun toProxy() = Proxy(protocol, InetSocketAddress(ip, port))
//
//
//}
//
//suspend fun  Request.await(): ByteArray = suspendCoroutine { cont ->
//    response({req, resp, result ->
//        when (result) {
//            is Result.Success -> cont.resume(result.value)
//            is Result.Failure -> cont.resumeWithException(result.error)
//        }
//
//    })
//}
//
//fun <T> List<T>.rand() = get(Random().nextInt(size))
//
//val proxyList = listOf(
//    Proxy(Proxy.Type.HTTP, InetSocketAddress("212.237.14.168", 8080)),
//    Proxy(Proxy.Type.SOCKS, InetSocketAddress("148.251.238.124", 1080)),
//    Proxy(Proxy.Type.HTTP, InetSocketAddress("35.195.65.241",3128 )),
//    Proxy(Proxy.Type.HTTP, InetSocketAddress("217.182.67.82", 3128 ))
//
//)
//suspend fun retrieveProxy()  = runBlocking {
//
//    val proxy = proxyList.rand().also { println("used $it") }
//    FuelManager.instance.proxy = proxy
//    val ba: ByteArray = Fuel.get("https://gimmeproxy.com/api/getProxy").await()
//    ProxyInfo.fromString(String(ba))
//}
//
////fun proxySequence() = buildSequence {
////    while (true) {
////        yield(runBlocking {
////            try {
////                val response: ByteArray =
////                return@runBlocking
////            } catch (e: Exception) {
////                print(e)
////                return@runBlocking null
////            }
////        })
////
////
////    }
////}
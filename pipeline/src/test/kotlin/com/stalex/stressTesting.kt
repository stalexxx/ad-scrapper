package com.stalex

import com.stalex.pipeline.RefItem
import com.stalex.pipeline.RefItemImpl
import com.stalex.pipeline.Scrap
import com.stalex.pipeline.ScrapParser
import com.stalex.pipeline.SyncObservable
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext

fun main(args: Array<String>) {
    val size = 10000

    val channel: ReceiveChannel<RefItem> = produce {
        (0 until size).forEach {
            send(RefItemImpl("$it"))
        }
    }

    val parsedCount = AtomicInteger()
    val loader: ScrapParser<RefItem, Scrap> = object : ScrapParser<RefItem, Scrap> {
        override suspend fun parse(page: RefItem): Scrap {
            delay(10)
            parsedCount.incrementAndGet()
            val value: Scrap = object : Scrap {}
            return value
        }
    }

    runBlockingBM {
        SyncObservable(
            channel,
            loader
        ).subscribe({
            println("observing in ${Thread.currentThread()}")
        })

        println("parsedCount $parsedCount")
    }
}

fun <T> runBlockingBM(context: CoroutineContext = EmptyCoroutineContext, block: suspend CoroutineScope.() -> T): T {
    val time = System.currentTimeMillis()
    val runBlocking = runBlocking(context, block)
    println("elapsed: ${System.currentTimeMillis() - time}")
    return runBlocking
}
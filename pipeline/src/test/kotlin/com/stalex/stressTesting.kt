package com.stalex

import com.stalex.pipeline.RefItem
import com.stalex.pipeline.RefItemImpl
import com.stalex.pipeline.RefPage
import com.stalex.pipeline.RefPageImpl
import com.stalex.pipeline.RefPageProvider
import com.stalex.pipeline.Scrap
import com.stalex.pipeline.ScrapCollectionParser
import com.stalex.pipeline.ScrapParser
import com.stalex.pipeline.SyncObservable
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext

fun main(args: Array<String>) {
    val pageProvider: RefPageProvider<RefPageImpl> = object : RefPageProvider<RefPageImpl> {
        override fun get(index: Int): RefPageImpl = RefPageImpl("url")
    }

    val itemProvider: ScrapCollectionParser<RefPage, RefItem> = object : ScrapCollectionParser<RefPage, RefItem> {
        var id = 0

        suspend override fun parse(page: RefPage): List<RefItem> {
            return (0 until 50).map { id += 1; RefItemImpl("$id"); }
        }
    }

    val parsedCount = AtomicInteger()
    val loader: ScrapParser<RefItem, Scrap> = object : ScrapParser<RefItem, Scrap> {
        suspend override fun parse(page: RefItem): Scrap {
            delay(10)
            parsedCount.incrementAndGet()
            val value: Scrap = object : Scrap {}
            return value
        }
    }

    runBlockingBM {
        var counter = 0
        SyncObservable(
            pageProvider,
            itemProvider,
            loader,
            {
                counter++ > 10000
            }
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
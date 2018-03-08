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
import io.kotlintest.specs.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking

class LasySeqAbstractionTest : StringSpec() {
    init {
        val pageProvider: RefPageProvider<RefPageImpl> = mockk()
        every { pageProvider.get(any()) } returns RefPageImpl("url")

        val itemProvider: ScrapCollectionParser<RefPage, RefItem> = mockk()
        var id = 0
        coEvery { itemProvider.parse(any()) } coAnswers {
            delay(1000)
            (0 until 5).map { id += 1; RefItemImpl("$id"); }
        }

        val loader: ScrapParser<RefItem, Scrap> = mockk(relaxed = true)
        coEvery {
            loader.parse(any())
        } coAnswers {
            delay(1000)
            mockk(relaxed = true)
        }

        "itemSeq coroutine test" {
            val seq = SyncObservable(
                pageProvider,
                itemProvider,
                loader
            ).itemRefProducer().iterator()
            runBlocking {
                (0 until 4).forEach {
                    seq.next()
                }
            }

            coVerify(exactly = 5) {
                loader.parse(any())
            } //на один больше потому что запись не совсем ленивая
        }.config(enabled = false)

        "sync observable test" {
            var counter = 0

            runBlocking {

                SyncObservable(
                    pageProvider,
                    itemProvider,
                    loader,
                    {
                        counter++ > 20
                    }
                ).subscribe({
                    println("observing in ${Thread.currentThread()}")
                })
            }
            coVerify(atLeast = counter - 1, atMost = counter + 1) { loader.parse(any()) }
        }.config(enabled = true)
    }
}
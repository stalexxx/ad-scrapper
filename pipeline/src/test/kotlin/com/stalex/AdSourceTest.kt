package com.stalex

import com.stalex.pipeline.AdLogger
import com.stalex.pipeline.AdSource
import com.stalex.pipeline.AdStorer
import com.stalex.pipeline.DefaultPipeline
import com.stalex.pipeline.RefItem
import com.stalex.pipeline.RefItemImpl
import com.stalex.pipeline.RefPage
import com.stalex.pipeline.RefPageImpl
import com.stalex.pipeline.RefPageProvider
import com.stalex.pipeline.Scrap
import com.stalex.pipeline.ScrapCollectionParser
import com.stalex.pipeline.ScrapParser
import com.stalex.pipeline.SyncObservable
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.experimental.runBlocking

class AdSourceTest : StringSpec() {
    init {

        val storer: AdStorer<Scrap> = mockk()
        coEvery { storer.handle(any()) } returns Unit
        val logger: AdLogger<Scrap> = mockk()
        coEvery { logger.handle(any()) } returns Unit

        val pipeline = DefaultPipeline<Scrap>()
            .withSource(
                object : AdSource<Scrap> {
                    suspend override fun subscribe(onNext: suspend (Scrap) -> Unit) {
                        (1..10).map { mockk<Scrap>(relaxed = true) }.forEach {
                            onNext(it)
                        }
                    }
                }
            )
            .with(storer)
            .with(logger)

        "verify size" {
            runBlocking {
                pipeline.start()

                coVerify(exactly = 10) {
                    logger.handle(any())
                    storer.handle(any())
                }
            }
        }
    }
}

class LasySeqAbstractionTest : StringSpec() {
    init {
        val pageProvider: RefPageProvider<RefPageImpl> = mockk()
        every { pageProvider.get(any()) } returns RefPageImpl("url")

        val itemProvider: ScrapCollectionParser<RefPage, RefItem> = mockk()
        every { itemProvider.parse(any()) } returns (0 until 5).map { RefItemImpl("$it") }

        val loader: ScrapParser<RefItem, Scrap> = mockk(relaxed = true)
        every {
            loader.parse(any())
        } returns mockk(relaxed = true)

        "itemSeq coroutine test" {
            val seq = SyncObservable(
                pageProvider,
                itemProvider,
                loader
            ).itemProducer().iterator()
            runBlocking {
                (0 until 4).forEach {
                    seq.next()
                }
            }

            verify(exactly = 5) { loader.parse(any()) } //на один больше потому что запись не совсем ленивая
        }.config(enabled = true)

        "sync observable test" {
            var counter = 0

            runBlocking {

                SyncObservable(
                    pageProvider,
                    itemProvider,
                    loader,
                    {
                        counter < 20
                    }
                ).subscribe({
                    println("verifing in ${Thread.currentThread()}")
                    counter++
                    verify { loader.parse(any()) }
                })
            }
            counter shouldBe 50
        }.config(enabled = false)
    }
}

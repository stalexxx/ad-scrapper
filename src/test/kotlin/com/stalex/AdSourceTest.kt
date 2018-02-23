package com.stalex

import com.stalex.avito.AvitoSourceItem
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

        val storer: AdStorer<AvitoSourceItem> = mockk()
        coEvery { storer.handle(any()) } returns Unit
        val logger: AdLogger<AvitoSourceItem> = mockk<ConsoleAdLogger>()
        coEvery { logger.handle(any()) } returns Unit

        val pipeline = DefaultPipeline<AvitoSourceItem>()
            .withSource(
                object : AdSource<AvitoSourceItem> {
                    suspend override fun subscribe(handler: suspend (AvitoSourceItem) -> Unit) {
                        (1..10).map { AvitoSourceItem("url") }.forEach {
                            handler(it)
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

        val itemProvider: RefItemProvider<RefPage, RefItem> = mockk()
        every { itemProvider.get(any()) } returns (1..5).map { RefItemImpl("$it") }

        val loader: EndItemProvider<RefItem, SourceItem> = mockk()
        every { loader.load(any()) } returns AvitoSourceItem("url")

        "itemSeq coroutine test" {
            val seq = SyncObservable(
                pageProvider,
                itemProvider,
                loader
            ).itemSeq().iterator()
            (1..20).forEach {
                seq.next()
            }

            verify(exactly = 20) { loader.load(any()) }
        } //.config(enabled = false)

        "sync observable test" {
            var counter = 0

            runBlocking {

                SyncObservable(
                    pageProvider,
                    itemProvider,
                    loader,
                    {
                        counter++
                        counter < 20
                    }
                ).subscribe({
                    println("verifing in ${Thread.currentThread()}")

                    verify { loader.load(any()) }
                })

                counter shouldBe 20
            }
        }
    }
}

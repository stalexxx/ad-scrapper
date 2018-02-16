package com.stalex

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class AdSourceTest : StringSpec() {
    init {

        val storer: AdStorer<AvitoItem> = mockk<MemoryStorer>()
        every { storer.handle(any()) } returns Unit
        val logger: AdLogger<AvitoItem> = mockk<ConsoleAdLogger>()
        every { logger.handle(any()) } returns Unit

        val pipeline = DefaultPipeline<AvitoItem>()
            .withSource(
                object : AdSource<AvitoItem> {
                    override fun subscribe(handler: (AvitoItem) -> Unit) {
                        (1..100).map { AvitoItem() }.forEach(handler)
                    }
                }
            )
            .with(storer)
            .with(logger)

        "verify size" {
            pipeline.start()
            verify(exactly = 100) {
                logger.handle(any())
                storer.handle(any())
            }
        }
    }
}

class LasySeqAbstractionTest : StringSpec() {
    init {
        val pageProvider: RefPageProvider = mockk()
        every { pageProvider.invoke(any()) } returns RefPageImpl()

        val itemProvider: RefItemProvider = mockk()
        every { itemProvider.invoke(any()) } returns (1 .. 5).map { RefItemImpl("$it") }

        val loader: EndItemLoader<AvitoItem> = mockk()
        every { loader.load(any()) } returns AvitoItem()


        "itemSeq coroutine test" {
            val seq = SyncObservable(
                pageProvider,
                itemProvider,
                loader
            ).itemSeq().iterator()
            (1 .. 20).forEach {
                seq.next()
            }

            verify(exactly = 20) { loader.load(any()) }
        }//.config(enabled = false)

        "sync observable test" {
            var counter = 0

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

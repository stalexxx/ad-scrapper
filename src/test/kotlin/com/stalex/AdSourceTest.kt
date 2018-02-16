package com.stalex

//import com.nhaarman.mockitokotlin2.mock
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxkotlin.toObservable

class AdSourceTest : StringSpec() {
    init {

        val storer: AdStorer<AvitoItem> = mockk<MemoryStorer>()
        every { storer.handle(any()) } returns Unit
        val logger: AdLogger<AvitoItem> = mockk<ConsoleAdLogger>()
        every { logger.handle(any()) } returns Unit

        val pipeline = DefaultPipeline<AvitoItem>()
            .withSource(
                ObservableSource(
                    (1..100).map { AvitoItem() }.toObservable()
                ))
            .with(storer)
            .with(logger)

        "verify size" {
            //            pipeline.chain.size shouldBe 3
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
        every { itemProvider.invoke(any()) } returns (1 .. 5).map { RefItemImpl() }

        val loader: EndItemLoader = mockk()
        every { loader.invoke(any()) } returns AvitoItem()

        val seq = itemSeq(
            pageProvider,
            itemProvider,
            loader
        ).iterator()
        (1 .. 20).forEach {
            seq.next()
        }
        "itemSeq coroutine test" {

            verify(exactly = 20) { loader.invoke(any()) }
        }

    }

}

package com.stalex

import com.stalex.pipeline.AdLogger
import com.stalex.pipeline.AdStorer
import com.stalex.pipeline.DefaultPipeline
import com.stalex.pipeline.ObservableSource
import com.stalex.pipeline.Scrap
import io.kotlintest.specs.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.experimental.runBlocking

class ObservableSourceTest : StringSpec() {
    init {

        val storer: AdStorer<Scrap> = mockk()
        coEvery { storer.handle(any()) } returns Unit
        val logger: AdLogger<Scrap> = mockk()
        coEvery { logger.handle(any()) } returns Unit

        val pipeline = DefaultPipeline<Scrap>()
            .withSource(
                object : ObservableSource<Scrap> {
                    override suspend fun subscribe(onNext: suspend (Scrap) -> Unit, onError: (Throwable) -> Unit) {
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
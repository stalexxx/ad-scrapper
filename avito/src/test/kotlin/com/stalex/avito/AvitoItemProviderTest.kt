package com.stalex.avito

import com.github.salomonbrys.kodein.instance
import com.stalex.pipeline.PageRefImpl
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.experimental.runBlocking
import nolambda.skrape.SkrapeLogger

class AvitoScrapProviderTest : StringSpec() {
    init {
        SkrapeLogger.enableLog = false

        "test size file" {
            runBlocking {
                val file = javaClass.classLoader.getResource("nedvizhimost.kt").file

                val provider = AvitoSourceItemProvider(kodein.instance())

                val result = provider.parse(PageRefImpl(file))
                result.size shouldBe 51
            }
        }

        "test size url" {
            runBlocking {

                val urlProvider = AvitoSourceItemProvider(kodein.instance())
                val urlResult = urlProvider.parse(PageRefImpl("https://www.avito.ru/sankt-peterburg/nedvizhimost.kt"))

                urlResult.size should {
                    it in 1..100
                }
            }
        }.config(invocations = 1, enabled = false)
    }
}
package com.stalex.avito

import com.stalex.pipeline.RefPageImpl
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import nolambda.skrape.SkrapeLogger

class AvitoSourceItemProviderTest : StringSpec() {
    init {
        SkrapeLogger.enableLog = false

        val file = javaClass.classLoader.getResource("nedvizhimost.kt").file
        val provider = AvitoSourceItemProvider()
        val result = provider.get(RefPageImpl(file))
        "test size file" {
            result.size shouldBe 51
        }

        val urlProvider = AvitoSourceItemProvider()
        val urlResult = urlProvider.get(RefPageImpl("https://www.avito.ru/sankt-peterburg/nedvizhimost.kt"))
        "test size url" {
            urlResult.size should {
                it in 1..100
            }
        }.config(invocations = 1)
    }
}
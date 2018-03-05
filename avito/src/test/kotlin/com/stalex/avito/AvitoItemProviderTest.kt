package com.stalex.avito

import com.github.salomonbrys.kodein.instance
import com.stalex.pipeline.RefPageImpl
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import nolambda.skrape.SkrapeLogger

class AvitoScrapProviderTest : StringSpec() {
    init {
        SkrapeLogger.enableLog = false

        "test size file" {
            val file = javaClass.classLoader.getResource("nedvizhimost.kt").file

            val provider = AvitoSourceItemProvider(kodein.instance())

            val result = provider.parse(RefPageImpl(file))
            result.size shouldBe 51
        }

        "test size url" {

            val urlProvider = AvitoSourceItemProvider(kodein.instance())
            val urlResult = urlProvider.parse(RefPageImpl("https://www.avito.ru/sankt-peterburg/nedvizhimost.kt"))

            urlResult.size should {
                it in 1..100
            }
        }.config(invocations = 1, enabled = false)
    }
}
package com.stalex.avito

import com.stalex.RefPageImpl
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class AvitoItemProviderTest : StringSpec() {
    init {
        val file = javaClass.classLoader.getResource("nedvizhimost").file
        val provider = AvitoItemProvider(file)
        val result = provider(RefPageImpl())
        "test size file" {
            result.size shouldBe 51
        }

        val urlProvider = AvitoItemProvider("https://www.avito.ru/sankt-peterburg/nedvizhimost")
        val urlResult = urlProvider(RefPageImpl())
        "test size url" {
            urlResult.size should {
                it in 1..100
            }
        }.config(invocations = 1)
    }
}
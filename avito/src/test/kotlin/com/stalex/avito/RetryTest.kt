package com.stalex.avito

import com.stalex.pipeline.Parser
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk
import nolambda.skrape.SkrapeLogger

class RetryTest : StringSpec() {
    init {
        SkrapeLogger.enableLog = false

        "test always ok" {
            val parser = mockk<Parser<String, Int>>()
            every { parser.parse(any()) } returns 1
            val result = RetryableParser(parser, default = { -1 }).parse("")

            result shouldBe 1
        }.config(enabled = true)

        "test always bad" {
            val parser = object : Parser<String, Int> {
                override fun parse(page: String): Int = throw IllegalStateException()
            }
            val result = RetryableParser(parser, default = { -1 }).parse("")

            result shouldBe -1
        }.config()

        "test always bad with mockk" {
            val parser = mockk<Parser<String, Int>>()
            every { parser.parse(any()) } throws IllegalStateException()

            val result = RetryableParser(parser, default = { -1 }).parse("")

            result shouldBe -1
        }.config(enabled = true)
    }
}
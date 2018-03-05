package com.stalex.avito

import com.stalex.pipeline.Parser
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk

class TryTest : StringSpec() {
    init {
        "test ok" {
            Try { 1 } should {
                (it as Try.Success).result shouldBe 1
            }
        }

        "test exception" {
            Try {
                fun x(): Int = throw IllegalStateException()
                x()
            } should {
                val error = (it as Try.Error).error
                (error is IllegalStateException) shouldBe true
            }
        }

        "test mocking" {
            val parser = mockk<Parser<String, Int>>()
            every { parser.parse(any()) } throws IllegalStateException()
            parser.parse("some staff")

            Try {
                parser.parse("some staff")
            } should {
                val error = (it as Try.Error).error
                (error is IllegalStateException) shouldBe true
            }
        }
    }
}
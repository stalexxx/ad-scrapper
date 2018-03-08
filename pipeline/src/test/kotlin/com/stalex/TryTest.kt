package com.stalex

import com.stalex.pipeline.Parser
import com.stalex.pipeline.Try
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.experimental.runBlocking

class TryTest : StringSpec() {
    init {
        "test ok" {
            runBlocking {
                Try { 1 } should {
                    (it as Try.Success).result shouldBe 1
                }
            }
        }

        "test exception" {
            runBlocking {
                Try {
                    fun x(): Int = throw IllegalStateException()
                    x()
                } should {
                    val error = (it as Try.Error).error
                    (error is IllegalStateException) shouldBe true
                }
            }
        }

        "test mocking" {
            val parser = mockk<Parser<String, Int>>()
            coEvery { parser.parse(any()) } throws IllegalStateException()

            runBlocking {
                Try {
                    parser.parse("some staff")
                } should {
                    val error = (it as Try.Error).error
                    (error is IllegalStateException) shouldBe true
                }
            }
        }
    }
}
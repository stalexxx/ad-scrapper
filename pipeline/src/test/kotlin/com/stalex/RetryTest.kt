package com.stalex

import com.stalex.pipeline.Parser
import com.stalex.pipeline.Try
import com.stalex.pipeline.retryable
import com.stalex.pipeline.success
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.experimental.runBlocking

class RetryTest : StringSpec() {
    init {
        "test always ok" {
            runBlocking {

                val parser = mockk<Parser<String, Int>>()
                coEvery { parser.parse(any()) } returns 1
                val result = parser.retryable().parse("")

                result shouldBe 1.success()
            }
        }.config(enabled = true)

        "test always bad with mockk" {
            runBlocking {

                val parser = mockk<Parser<String, String>>()
                coEvery { parser.parse(any()) } throws IllegalStateException()

                val result = parser.retryable().parse("")

                (result as Try.Error).error should {
                    it is IllegalStateException
                }
            }
        }.config(enabled = true)
    }
}
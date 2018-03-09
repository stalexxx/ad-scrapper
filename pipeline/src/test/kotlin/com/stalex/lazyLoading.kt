package com.stalex

import com.stalex.pipeline.RefItem
import com.stalex.pipeline.RefItemImpl
import com.stalex.pipeline.Scrap
import com.stalex.pipeline.ScrapParser
import com.stalex.pipeline.SyncObservable
import io.kotlintest.specs.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking

class LasySeqAbstractionTest : StringSpec() {
    init {
        val size = 20

        val channel: ReceiveChannel<RefItem> = produce {
            (0 until size).forEach {
                send(RefItemImpl("$it"))
            }
        }

        val loader: ScrapParser<RefItem, Scrap> = mockk(relaxed = true)
        coEvery {
            loader.parse(any())
        } coAnswers {
            delay(1000)
            mockk(relaxed = true)
        }

//        "itemSeq coroutine test" {
//            val seq = SyncObservable(
//                channel,
//                loader
//            )
//            runBlocking {
//                (0 until 4).forEach {
////                    seq.next()
//                }
//            }
//
//            coVerify(exactly = 5) {
//                loader.parse(any())
//            } //на один больше потому что запись не совсем ленивая
//        }.config(enabled = false)

        "sync observable test" {
            var counter = 0

            runBlocking {

                SyncObservable(
                    channel,
                    loader
                ).subscribe({
                    println("observing in ${Thread.currentThread()}")
                })
            }
            coVerify(exactly = size) { loader.parse(any()) }
        }.config(enabled = true)
    }
}
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import kotlin.coroutines.experimental.buildSequence

inline fun printThreadName() = println("current thread (${Throwable().stackTrace[0]}}; ): ${Thread.currentThread().name}")

//fun main(args: Array<String>) {
//    launch { // launch new coroutine in background and continue
//        delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
//        printThreadName()
//        println("World!") // print after delay
//    }
//    printThreadName()
//    println("Hello,") // main thread continues while coroutine is delayed
//    Thread.sleep(2000L) // block main thread for 2 seconds to keep JVM alive
//}

fun main(args: Array<String>) = runBlocking {
    //    testJoin()
    testSeq()
}

suspend fun testSeq() {
    val seq = buildSequence {
        //        delay(1000)
        yieldAll((1..1000))
    }

    seq.forEach {
        delay(1000)
        print(it)
    }
}

private suspend fun testJoin() {
    printThreadName()

    val job = launch {
        // launch new coroutine and keep a reference to its Job
        printThreadName()

        delay(3000L)
        println("World!")
    }
    printThreadName()

    println("Hello,")
    job.join()
}
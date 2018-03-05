package com.stalex.avito

import com.google.gson.GsonBuilder
import com.stalex.pipeline.Parser
import nolambda.skrape.Skrape
import nolambda.skrape.nodes.ElementBody
import nolambda.skrape.nodes.Page

val gson = GsonBuilder().create()

//---skrape---
inline fun <reified T> Skrape<String>.requestPage(url: String, noinline body: ElementBody): T {
    return request(Page(url, body = body)).let {
        gson.fromJson(it, T::class.java)
    }
}

sealed class Try<T> {
    class Success<T>(val result: T) : Try<T>()
    class Error<T>(val error: Exception) : Try<T>()
}

fun <T> Try(block: () -> T): Try<T> {
    try {
        val result = block()
        return Try.Success(result)
    } catch (e: Exception) {
        return Try.Error(e)
    }
}

class RetryableParser<in T , out R>(
    val parser: Parser<T, R>,
    val retryCount: Int = 5,
    val default: () -> R
) : Parser<T, R> {
    override fun parse(page: T): R {

        (0 until retryCount).forEach {
            val result = Try { parser.parse(page) }
            when (result) {
                is Try.Success -> return result.result
            }
        }

        return default()
    }
}

fun ParsingFail(): Nothing = throw AdScrapperException.ParseException()

sealed class AdScrapperException : RuntimeException() {
    class ParseException() : AdScrapperException()
}

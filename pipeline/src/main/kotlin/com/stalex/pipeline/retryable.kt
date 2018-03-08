package com.stalex.pipeline

sealed class Try<out T> {
    data class Success<out T>(val result: T) : Try<T>()
    data class Error<out T>(val error: Exception) : Try<T>()

    fun fold(default: (Throwable) -> Unit): T? = when (this) {
        is Success -> result
        is Error -> {
            default(error)
            null
        }
    }
}

fun <T : Any> T.success(): Try<T> = Try.Success(this)

suspend fun <T> Try(block: suspend () -> T): Try<T> =
    try {
        val result = block()
        Try.Success(result)
    } catch (e: Exception) {
        Try.Error(e)
    }

fun <T, R> Parser<T, R>.retryable() = RetryableParser(this)

class RetryableParser<in T, out R>(
    val parser: Parser<T, R>,
    val retryCount: Int = 5
) : Parser<T, Try<R>> {

    suspend override fun parse(page: T): Try<R> {

        var e: Exception? = null
        (0 until retryCount).forEach {
            val result = Try { parser.parse(page) }

            when (result) {
                is Try.Success -> return result
                is Try.Error -> e = result.error
            }
        }
        return Try.Error(e!!)
    }
}

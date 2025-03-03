package com.alplabs.barcodescanner.data.repository

/**
 * Created by Alfredo Lima Porfirio on 13/03/23.
 */
sealed class Result<V>(
    private val successValue: V?,
    private val error: Throwable?
) {
    fun get() = successValue!!
    fun getOrNull() = successValue

    fun exception() = error!!
    fun exceptionOrNull() = error

    val isSuccess get() = successValue != null
    val isFailure get() = error != null

    internal class Success<V>(value: V) : Result<V>(value, null)
    internal class Fail<V>(error: Throwable) : Result<V>(null, error)

    companion object {
        fun <V>success(value: V): Result<V> = Success(value)
        fun <V>failure(exception: Throwable): Result<V> = Fail(exception)
    }
}

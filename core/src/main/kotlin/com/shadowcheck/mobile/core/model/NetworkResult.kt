package com.shadowcheck.mobile.core.model

/**
 * A generic sealed class to represent the result of network operations.
 * Provides type-safe handling of success, error, and loading states.
 *
 * @param T The type of data expected on success.
 */
sealed class NetworkResult<out T> {
    /**
     * Represents a successful network operation with data.
     *
     * @param data The successful result data.
     */
    data class Success<out T>(val data: T) : NetworkResult<T>()

    /**
     * Represents a failed network operation with an error.
     *
     * @param exception The exception that caused the failure.
     * @param message A human-readable error message.
     */
    data class Error(
        val exception: Throwable,
        val message: String = exception.message ?: "Unknown error occurred"
    ) : NetworkResult<Nothing>()

    /**
     * Represents a loading state during a network operation.
     */
    data object Loading : NetworkResult<Nothing>()
}

/**
 * Extension function to check if the result is successful.
 */
fun <T> NetworkResult<T>.isSuccess(): Boolean = this is NetworkResult.Success

/**
 * Extension function to check if the result is an error.
 */
fun <T> NetworkResult<T>.isError(): Boolean = this is NetworkResult.Error

/**
 * Extension function to check if the result is loading.
 */
fun <T> NetworkResult<T>.isLoading(): Boolean = this is NetworkResult.Loading

/**
 * Extension function to get data or null.
 */
fun <T> NetworkResult<T>.getDataOrNull(): T? = when (this) {
    is NetworkResult.Success -> data
    else -> null
}

/**
 * Extension function to get data or a default value.
 */
fun <T> NetworkResult<T>.getDataOrDefault(default: T): T = when (this) {
    is NetworkResult.Success -> data
    else -> default
}

/**
 * Extension function to execute an action on success.
 */
inline fun <T> NetworkResult<T>.onSuccess(action: (T) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Success) {
        action(data)
    }
    return this
}

/**
 * Extension function to execute an action on error.
 */
inline fun <T> NetworkResult<T>.onError(action: (Throwable, String) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Error) {
        action(exception, message)
    }
    return this
}

/**
 * Extension function to execute an action on loading.
 */
inline fun <T> NetworkResult<T>.onLoading(action: () -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Loading) {
        action()
    }
    return this
}

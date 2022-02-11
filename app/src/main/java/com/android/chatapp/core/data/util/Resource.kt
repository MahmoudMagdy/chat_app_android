package com.android.chatapp.core.data.util

import kotlinx.serialization.Serializable

sealed class Resource<out T, out E> {
    @Serializable
    data class Success<out T>(val data: T) : Resource<T, Nothing>()

    @Serializable
    data class Failure<out T, out E>(val errors: List<E>, val data: T? = null) : Resource<T, E>()

    data class Error<out T>(val throwable: Throwable, val data: T? = null) : Resource<T, Nothing>()

    data class Loading<out T>(val data: T? = null) : Resource<T, Nothing>()
}

inline fun <reified T, reified E> Resource<T, E>.data(): T? = when (this) {
    is Resource.Error -> data
    is Resource.Failure -> data
    is Resource.Loading -> data
    is Resource.Success -> data
}

//val Resource.Error<Any>.errors get() = listOf(throwable)
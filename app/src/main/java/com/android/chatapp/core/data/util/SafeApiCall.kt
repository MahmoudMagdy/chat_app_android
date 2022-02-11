package com.android.chatapp.core.data.util

import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.features.websocket.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

suspend inline fun <reified T, reified E> safeApiCall(crossinline apiCall: suspend () -> Resource.Success<T>): Resource<T, E> {
    return withContext(Dispatchers.IO) {
        try {
            apiCall.invoke()
        } catch (throwable: Throwable) {
            when (throwable) {
                is RedirectResponseException -> {
                    //3xx - responses
                    throwable.response.receive<Resource.Failure<T, E>>()
                }
                is ClientRequestException -> {
                    //4xx - responses
                    throwable.response.receive<Resource.Failure<T, E>>()
                }
                is ServerResponseException -> {
                    //5xx - responses
                    Resource.Error(throwable)
                }
                is NetworkResponseException -> {
                    //no_network - responses
                    Resource.Error(throwable)
                }
                else -> {
                    //unknown - responses
                    Resource.Error(throwable)
                }
            }
        }
    }
}

suspend inline fun safeApiConnection(crossinline apiConnection: suspend () -> DefaultClientWebSocketSession): Resource<Unit, Nothing> {
    return withContext(Dispatchers.IO) {
        try {
            val socket = apiConnection.invoke()
            if (socket.isActive) Resource.Success(Unit)
            else Resource.Error(ConnectionNotEstablishedException())
        } catch (throwable: Throwable) {
            when (throwable) {
                is ConnectTimeoutException, is NetworkResponseException ->
                    Resource.Error(NetworkResponseException())
                else -> Resource.Error(throwable)
            }
        }
    }
}

suspend inline fun safeApiMessaging(crossinline apiMessaging: suspend () -> Unit): Resource<Unit, Nothing> {
    return withContext(Dispatchers.IO) {
        try {
            apiMessaging.invoke()
            Resource.Success(Unit)
        } catch (throwable: Throwable) {
            when (throwable) {
                is ConnectTimeoutException, is NetworkResponseException ->
                    Resource.Error(NetworkResponseException())
                else -> Resource.Error(throwable)
            }
        }
    }
}
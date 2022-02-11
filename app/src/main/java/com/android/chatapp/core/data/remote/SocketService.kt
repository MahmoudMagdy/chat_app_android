package com.android.chatapp.core.data.remote

import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.flow.Flow

interface SocketService<out RESPONSE> {
    val connected: Boolean
    suspend fun disconnect(block: (CloseReason?) -> Unit)
    fun receive(): Flow<RESPONSE>
    suspend fun close()
}


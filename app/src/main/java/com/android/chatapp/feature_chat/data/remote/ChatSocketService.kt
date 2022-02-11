package com.android.chatapp.feature_chat.data.remote

import com.android.chatapp.core.data.remote.SocketService
import com.android.chatapp.feature_chat.data.remote.dto.MessageRequest
import com.android.chatapp.feature_chat.data.remote.dto.MessageResponse
import io.ktor.client.features.websocket.*

interface ChatSocketService : SocketService<MessageResponse> {
    suspend fun connect(id: Long): DefaultClientWebSocketSession
    suspend fun send(message: MessageRequest)
}
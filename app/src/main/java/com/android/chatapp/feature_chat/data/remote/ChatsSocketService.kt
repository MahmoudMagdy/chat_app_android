package com.android.chatapp.feature_chat.data.remote

import com.android.chatapp.core.data.remote.SocketService
import com.android.chatapp.feature_chat.data.remote.dto.ChatResponse
import io.ktor.client.features.websocket.*

interface ChatsSocketService : SocketService<ChatResponse> {
    suspend fun connect(): DefaultClientWebSocketSession
}
package com.android.chatapp.feature_chat.data.remote

import com.android.chatapp.core.data.remote.SocketServiceImpl
import com.android.chatapp.feature_chat.data.remote.dto.ChatResponse
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

/**
 *  @param client is the authenticated one.
 **/
class ChatsSocketServiceImpl(client: HttpClient, serializer: Json) :
    SocketServiceImpl<Unit, ChatResponse>(
        client, serializer, typeOf<Unit>(), typeOf<ChatResponse>()
    ), ChatsSocketService {
    override suspend fun connect(): DefaultClientWebSocketSession = connect(WSRoutes.CHAT_LIST_URL)
}
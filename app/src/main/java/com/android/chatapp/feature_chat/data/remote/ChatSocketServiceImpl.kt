package com.android.chatapp.feature_chat.data.remote

import com.android.chatapp.core.data.remote.SocketServiceImpl
import com.android.chatapp.feature_chat.data.remote.dto.MessageRequest
import com.android.chatapp.feature_chat.data.remote.dto.MessageResponse
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

/**
 *  @param client is the authenticated one.
 **/
class ChatSocketServiceImpl(client: HttpClient, serializer: Json) :
    SocketServiceImpl<MessageRequest, MessageResponse>(
        client, serializer,
        typeOf<MessageRequest>(), typeOf<MessageResponse>()
    ), ChatSocketService {

    override suspend fun connect(id: Long): DefaultClientWebSocketSession =
        connect(WSRoutes.getChatUrl(id))

    override suspend fun send(message: MessageRequest) = _send(message)
}
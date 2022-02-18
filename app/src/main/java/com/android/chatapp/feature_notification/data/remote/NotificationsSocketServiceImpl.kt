package com.android.chatapp.feature_notification.data.remote

import com.android.chatapp.core.data.remote.SocketServiceImpl
import com.android.chatapp.feature_notification.data.remote.dto.NotificationResponse
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.serializer
import kotlin.reflect.typeOf

class NotificationsSocketServiceImpl(client: HttpClient, serializer: Json) :
    SocketServiceImpl<Unit, NotificationResponse<*>>(
        client, serializer, typeOf<Unit>(), typeOf<NotificationResponse<JsonObject>>()
    ), NotificationsSocketService {
    override suspend fun connect(): DefaultClientWebSocketSession =
        connect(WSRoutes.NOTIFICATION_LIST_URL)

    override fun receive(): Flow<NotificationResponse<*>> =
        super.receive().map { notification: NotificationResponse<*> ->
            notification as NotificationResponse<JsonObject>
            val type = notification.type.type
            val data = serializer.decodeFromJsonElement(
                serializer.serializersModule.serializer(type), notification.data
            )
            NotificationResponse(notification.type, data)
        }

}
package com.android.chatapp.feature_notification.data.remote

import com.android.chatapp.core.data.remote.SocketService
import com.android.chatapp.feature_notification.data.remote.dto.NotificationResponse
import io.ktor.client.features.websocket.*

interface NotificationsSocketService:SocketService<NotificationResponse<*>> {
    suspend fun connect(): DefaultClientWebSocketSession
}
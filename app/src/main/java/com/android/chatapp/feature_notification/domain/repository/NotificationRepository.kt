package com.android.chatapp.feature_notification.domain.repository

import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.core.data.util.WSCloseReason
import com.android.chatapp.feature_notification.data.remote.dto.NotificationResponse
import com.android.chatapp.feature_notification.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun connect(): Resource<Unit, Nothing>
    suspend fun disconnect(block: (WSCloseReason) -> Unit)
    fun receive(): Flow<Notification>
    suspend fun close()
}
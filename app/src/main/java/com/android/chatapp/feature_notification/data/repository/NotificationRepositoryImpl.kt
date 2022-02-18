package com.android.chatapp.feature_notification.data.repository

import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.core.data.util.WSCloseReason
import com.android.chatapp.core.data.util.safeApiConnection
import com.android.chatapp.feature_chat.data.local.MessageDao
import com.android.chatapp.feature_notification.data.remote.NotificationsSocketService
import com.android.chatapp.feature_notification.data.remote.dto.NMessageResponse
import com.android.chatapp.feature_notification.data.remote.dto.entity
import com.android.chatapp.feature_notification.data.remote.dto.model
import com.android.chatapp.feature_notification.domain.model.Notification
import com.android.chatapp.feature_notification.domain.model.NotificationType
import com.android.chatapp.feature_notification.domain.repository.NotificationRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class NotificationRepositoryImpl(
    private val socketService: NotificationsSocketService,
    private val messageDao: MessageDao
) : NotificationRepository {
    override suspend fun connect(): Resource<Unit, Nothing> =
        safeApiConnection(socketService::connect)

    override suspend fun disconnect(block: (WSCloseReason) -> Unit) =
        socketService.disconnect { reason -> block(WSCloseReason by reason) }


    override fun receive(): Flow<Notification> =
        socketService.receive()
            .map { notification ->
                when (notification.type) {
                    NotificationType.NEW_MESSAGE -> onNewMessageAsync(notification.data)
                }
            }


    @Suppress("DeferredResultUnused")
    private suspend fun onNewMessageAsync(message: Any?): Notification {
        message as NMessageResponse
        coroutineScope {
            async { messageDao.insert(message.entity) }
        }
        return message.model
    }


    override suspend fun close() = socketService.close()

}
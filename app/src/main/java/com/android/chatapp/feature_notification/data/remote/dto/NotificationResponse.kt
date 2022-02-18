package com.android.chatapp.feature_notification.data.remote.dto

import com.android.chatapp.feature_notification.domain.model.NotificationType
import kotlinx.serialization.Serializable

@Serializable
data class NotificationResponse<out T>(val type:NotificationType, val data:T)

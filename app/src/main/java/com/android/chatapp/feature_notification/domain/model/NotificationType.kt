package com.android.chatapp.feature_notification.domain.model

import com.android.chatapp.feature_notification.data.remote.dto.NMessageResponse
import kotlin.reflect.KType
import kotlin.reflect.typeOf

enum class NotificationType(val type: KType) {
    NEW_MESSAGE(typeOf<NMessageResponse>()),
}
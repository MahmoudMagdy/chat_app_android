package com.android.chatapp.feature_notification.domain.util

import com.android.chatapp.feature_notification.data.remote.dto.NMessageResponse

sealed class ReceiveEvent{
    data class OnNewMessage(val message:NMessageResponse):ReceiveEvent()
}

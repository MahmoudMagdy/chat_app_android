package com.android.chatapp.feature_chat.data.remote.dto

import com.android.chatapp.feature_chat.domain.model.MessageType
import kotlinx.serialization.Serializable

@Serializable
data class MessageRequest(val type: MessageType, val content: String)
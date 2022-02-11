package com.android.chatapp.feature_chat.data.remote.dto

import com.android.chatapp.feature_chat.data.local.entity.MessageEntity
import com.android.chatapp.feature_chat.domain.model.Message
import com.android.chatapp.feature_chat.domain.model.MessageType
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    val id: Long,
    @SerialName("user_id")
    val userId: Long,
    @SerialName("chat_id")
    val chatId: Long,
    val type: MessageType,
    val content: String,
    @SerialName("created_at")
    val createdAt: Instant
)

val MessageResponse.entity get() = MessageEntity(id, userId, chatId, type, content, createdAt)
val MessageResponse.model get() = Message(id, userId, content, type, createdAt)
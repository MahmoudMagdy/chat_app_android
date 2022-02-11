package com.android.chatapp.feature_chat.data.remote.dto

import com.android.chatapp.feature_authentication.data.remote.dto.UserResponse
import com.android.chatapp.feature_chat.data.local.entity.ChatEntity
import com.android.chatapp.feature_chat.domain.model.ChatType
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
    val id: Long,
    val type: ChatType,
    val title: String?,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("updated_at")
    val updatedAt: Instant,
    @SerialName("latest_message")
    val latestMessage: MessageResponse,
    val users: List<UserResponse>
)


val ChatResponse.entity get() = ChatEntity(id, type, title, createdAt, updatedAt)

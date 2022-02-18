package com.android.chatapp.feature_notification.data.remote.dto

import android.content.Intent
import androidx.core.net.toUri
import com.android.chatapp.R
import com.android.chatapp.core.presentation.util.EMPTY_TEXT
import com.android.chatapp.feature_authentication.data.remote.dto.UserResponse
import com.android.chatapp.feature_chat.data.local.entity.MessageEntity
import com.android.chatapp.feature_chat.data.remote.dto.ChatResponse
import com.android.chatapp.feature_chat.domain.model.MessageType
import com.android.chatapp.feature_chat.presentation.linkToChat
import com.android.chatapp.feature_notification.domain.model.Notification
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NMessageResponse(
    val id: Long,
    val user: UserResponse,
    val chat: ChatResponse,
    val type: MessageType,
    val content: String,
    @SerialName("created_at")
    val createdAt: Instant
)

val NMessageResponse.entity get() = MessageEntity(id, user.id, chat.id, type, content, createdAt)
val NMessageResponse.model
    get() = Notification(
        chat.id.toInt(),
        user.profile?.run { "$firstName $lastName" } ?: EMPTY_TEXT,
        content,
        user.profile?.latestImage?.media,
        R.drawable.def_profile_pic,
        Intent(Intent.ACTION_VIEW, linkToChat(cid = chat.id).toUri())
    )
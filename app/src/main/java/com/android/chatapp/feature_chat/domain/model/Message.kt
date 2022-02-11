package com.android.chatapp.feature_chat.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Message(
    val id: Long,
    val userId: Long,
    val content: String,
    val type: MessageType,
    val created_at: Instant
)

val MESSAGE_PREVIEW = Message(
    id = 1,
    userId = 1,
    content = "Hi, How are you doing now!",
    type = MessageType.TEXT,
    created_at = Clock.System.now()
)


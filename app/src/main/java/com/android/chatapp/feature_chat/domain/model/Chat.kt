package com.android.chatapp.feature_chat.domain.model

import com.android.chatapp.feature_authentication.domain.model.USER_PREVIEW
import com.android.chatapp.feature_authentication.domain.model.User

data class Chat(
    val id: Long,
    val type: ChatType,
    val title: String?,
    val user: User,
    val latestMessage: Message
)

val CHAT_PREVIEW = Chat(
    id = 1,
    type = ChatType.CONVERSATION,
    title = null,
    user = USER_PREVIEW,
    latestMessage = MESSAGE_PREVIEW
)
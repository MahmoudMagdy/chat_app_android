package com.android.chatapp.feature_chat.presentation.chat_list

import com.android.chatapp.feature_chat.presentation.chat_list.components.ChatItemEvent

sealed class ChatListEvent {
    data class ChatItemClicked(val event: ChatItemEvent) : ChatListEvent()
    data class UserSearchValueChanged(val value: String) : ChatListEvent()
    object UserSearchButtonClicked : ChatListEvent()
    object Logout : ChatListEvent()
}
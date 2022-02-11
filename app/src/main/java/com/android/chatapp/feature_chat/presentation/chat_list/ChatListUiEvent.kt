package com.android.chatapp.feature_chat.presentation.chat_list

import com.android.chatapp.feature_authentication.domain.model.User
import com.android.chatapp.feature_chat.domain.model.Chat

sealed class ChatListUiEvent {
    data class NavigateToChat(val chat: Chat) : ChatListUiEvent()
    data class NavigateToUserProfile(val user: User) : ChatListUiEvent()
    data class NavigateToSearchProfiles(val keyword: String) : ChatListUiEvent()
    object Logout : ChatListUiEvent()
}
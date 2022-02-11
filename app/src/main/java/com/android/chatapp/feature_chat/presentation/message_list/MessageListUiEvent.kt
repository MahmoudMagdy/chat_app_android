package com.android.chatapp.feature_chat.presentation.message_list

import com.android.chatapp.feature_authentication.domain.model.Profile
import com.android.chatapp.feature_authentication.domain.model.User
import com.android.chatapp.feature_chat.domain.model.Chat

sealed class MessageListUiEvent {
    data class NavigateToUserProfile(val profile: Profile) : MessageListUiEvent()
    object PopBackStack : MessageListUiEvent()
    object Logout : MessageListUiEvent()
}
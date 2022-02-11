package com.android.chatapp.feature_chat.presentation.message_list

import com.android.chatapp.feature_chat.presentation.message_list.components.ChatAppBarEvent

sealed class MessageListEvent {
    data class MessageValueChanged(val value: String) : MessageListEvent()
    data class OnAppBarEvent(val event: ChatAppBarEvent) : MessageListEvent()
    object SendButtonClicked : MessageListEvent()
}
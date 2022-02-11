package com.android.chatapp.feature_chat.domain.use_case

import com.android.chatapp.feature_authentication.domain.user_case.Logout
import javax.inject.Inject

data class ChatListUseCases @Inject constructor(
    val getChats: GetChats,
    val connectToChats: ConnectToChats,
    val closeChatsConnection: CloseChatsConnection,
    val logout: Logout
)
package com.android.chatapp.feature_chat.domain.use_case

import com.android.chatapp.feature_authentication.domain.user_case.Logout
import javax.inject.Inject

data class MessageListUseCases @Inject constructor(
    val getChat: GetChat,
    val getUser: GetUser,
    val getChatMessages: GetChatMessages,
    val connectToChatMessages: ConnectToChatMessages,
    val sendMessage: SendChatMessage,
    val closeChatMessagesConnection: CloseChatMessagesConnection,
    val logout: Logout
)
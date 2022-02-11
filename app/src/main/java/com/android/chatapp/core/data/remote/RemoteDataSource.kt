package com.android.chatapp.core.data.remote

import com.android.chatapp.feature_authentication.data.remote.AuthService
import com.android.chatapp.feature_chat.data.remote.ChatService
import com.android.chatapp.feature_chat.data.remote.ChatSocketService
import com.android.chatapp.feature_chat.data.remote.ChatsSocketService


interface RemoteDataSource {
    val authService: AuthService
    val storageService: StorageService
    val chatService: ChatService
    val chatsSocketService: ChatsSocketService
    val chatSocketService: ChatSocketService
}
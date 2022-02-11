package com.android.chatapp.feature_chat.domain.use_case

import com.android.chatapp.feature_chat.domain.repository.ChatRepository
import javax.inject.Inject

class CloseChatsConnection @Inject constructor(private val repository: ChatRepository) {

    suspend operator fun invoke() = repository.closeChats()
}
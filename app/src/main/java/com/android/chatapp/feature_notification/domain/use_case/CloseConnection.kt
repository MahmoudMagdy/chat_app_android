package com.android.chatapp.feature_notification.domain.use_case

import com.android.chatapp.feature_chat.domain.repository.ChatRepository
import com.android.chatapp.feature_notification.domain.repository.NotificationRepository
import javax.inject.Inject

class CloseConnection @Inject constructor(private val repository: NotificationRepository) {

    suspend operator fun invoke() = repository.close()
}
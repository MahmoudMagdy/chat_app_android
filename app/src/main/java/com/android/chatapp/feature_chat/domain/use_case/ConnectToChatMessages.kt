package com.android.chatapp.feature_chat.domain.use_case

import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.core.data.util.WSCloseReason
import com.android.chatapp.feature_chat.domain.model.Chat
import com.android.chatapp.feature_chat.domain.model.Message
import com.android.chatapp.feature_chat.domain.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class ConnectToChatMessages @Inject constructor(private val repository: ChatRepository) {

    suspend operator fun invoke(
        id: Long,
        scope: CoroutineScope,
        onReceive: (Message) -> Unit,
        onClose: (WSCloseReason) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        repository.connectToChatMessages(id).apply {
            when (this) {
                is Resource.Success -> {
                    scope.launch(Dispatchers.IO) {
                        repository.disconnectFromChatMessages(onClose)
                    }
                    scope.launch(Dispatchers.IO) {
                        repository.receiveChatMessages().collect(onReceive)
                    }
                }
                is Resource.Error -> onError(throwable)
                is Resource.Loading,
                is Resource.Failure -> Unit
            }
        }

    }
}
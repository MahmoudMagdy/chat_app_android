package com.android.chatapp.feature_chat.domain.use_case

import com.android.chatapp.core.data.util.WSCloseReason
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_chat.domain.model.Chat
import com.android.chatapp.feature_chat.domain.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class ConnectToChats @Inject constructor(private val repository: ChatRepository) {

    suspend operator fun invoke(
        scope: CoroutineScope,
        onReceive: (Chat) -> Unit,
        onClose: (WSCloseReason) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        repository.connectToChats().apply {
            when (this) {
                is Resource.Success -> {
                    scope.launch(Dispatchers.IO) {
                        repository.disconnectFromChats(onClose)
                    }
                    scope.launch(Dispatchers.IO) {
                        repository.receiveChats().collect(onReceive)
                    }
                }
                is Resource.Error -> onError(throwable)
                is Resource.Loading,
                is Resource.Failure -> Unit
            }
        }

    }
}
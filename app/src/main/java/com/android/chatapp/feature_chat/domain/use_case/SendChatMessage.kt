package com.android.chatapp.feature_chat.domain.use_case

import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_chat.data.remote.dto.MessageRequest
import com.android.chatapp.feature_chat.domain.model.Chat
import com.android.chatapp.feature_chat.domain.model.MessageType
import com.android.chatapp.feature_chat.domain.repository.ChatRepository
import com.android.chatapp.feature_chat.presentation.message_list.ScreenState
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class SendChatMessage @Inject constructor(private val repository: ChatRepository) {

    suspend operator fun invoke(
        content: String,
        screenState: ScreenState,
        onEvent: (SendChatEvent) -> Unit
    ) {
        val cleanContent = content.trim()
        if (cleanContent.isNotBlank()) {
            if (screenState.chat != null && repository.isConnectToChatMessages(-1)) {
                onEvent(SendChatEvent.CleanMessage)
                sendThroughConnection(cleanContent)
            } else if (screenState.user != null && !screenState.loading && !screenState.loadAbility) {
                onEvent(SendChatEvent.CleanMessage)
                sendThroughApiCall(screenState.user?.id!!, cleanContent, screenState, onEvent)
            }
        }
    }

    private suspend fun sendThroughConnection(content: String) {
        when (repository.sendChatMessages(MessageRequest(MessageType.TEXT, content))) {
            is Resource.Error -> {
                //TODO
            }
            is Resource.Success -> {
                //TODO
            }
            else -> Unit
        }
    }

    // FIXME: Handle other resources
    private suspend fun sendThroughApiCall(
        oid: Long,
        content: String,
        screenState: ScreenState,
        onEvent: (SendChatEvent) -> Unit
    ) {
        repository.createChat(oid, MessageRequest(MessageType.TEXT, content)).collect { result ->
            when (result) {
                is Resource.Success -> {
                    val chat = result.data
                    screenState.stopLoading
                    screenState.chat = chat
                    screenState.addMessage(chat.latestMessage)
                    onEvent(SendChatEvent.ChatCreated(result.data))
                }
                is Resource.Error -> {
                    screenState.stopLoading
                    //TODO
                }
                is Resource.Failure -> {
                    screenState.stopLoading
                    //TODO
                }
                is Resource.Loading -> screenState.startLoading
            }
        }
    }
}

sealed class SendChatEvent {
    data class ChatCreated(val chat: Chat) : SendChatEvent()
    object CleanMessage : SendChatEvent()
}
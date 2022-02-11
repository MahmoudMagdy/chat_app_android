package com.android.chatapp.feature_chat.presentation.message_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.chatapp.core.data.util.ConnectionNotEstablishedException
import com.android.chatapp.core.data.util.NetworkResponseException
import com.android.chatapp.core.data.util.WSCloseReason
import com.android.chatapp.core.data.util.data
import com.android.chatapp.feature_chat.domain.model.Message
import com.android.chatapp.feature_chat.domain.use_case.MessageListUseCases
import com.android.chatapp.feature_chat.domain.use_case.SendChatEvent
import com.android.chatapp.feature_chat.presentation.NO_ID
import com.android.chatapp.feature_chat.presentation.message_list.components.ChatAppBarEvent
import com.android.chatapp.feature_chat.presentation.util.ErrorEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageListViewModel @Inject constructor(
    private val cases: MessageListUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val chatId = savedStateHandle.get<Long>(CHAT_ID)
    private val userId = savedStateHandle.get<Long>(USER_ID)

    val screenState = ScreenState()

    private val _uiEvent = Channel<MessageListUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var message by mutableStateOf("")
        private set


    fun retrieveData() {
        if ((chatId != null && chatId != NO_ID) || (userId != null && userId != NO_ID)) {
            viewModelScope.launch(Dispatchers.IO) {
                cases.getChat(
                    cid = chatId,
                    oid = userId,
                    screenState = screenState,
                    getMessages = ::getChatMessages,
                    onErrorEvent = ::onErrorEvent
                )
            }
            if (userId != null && userId != NO_ID) viewModelScope.launch(Dispatchers.IO) {
                cases.getUser(userId).collect {
                    val result = it.data()
                    if (result != null && screenState.chat == null)
                        screenState.user = result
                }
            }
        } else sendUiEvent(MessageListUiEvent.PopBackStack)
    }

    private fun sendUiEvent(event: MessageListUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }


    fun onEvent(event: MessageListEvent) {
        when (event) {
            is MessageListEvent.MessageValueChanged -> message = event.value
            MessageListEvent.SendButtonClicked -> onSendClicked()
            is MessageListEvent.OnAppBarEvent -> onAppBarClicked(event.event)
        }
    }


    private fun onAppBarClicked(event: ChatAppBarEvent) {
        when (event) {
            ChatAppBarEvent.USER_CLICKED -> onUserClicked()
            ChatAppBarEvent.BACK_BUTTON_CLICKED -> sendUiEvent(MessageListUiEvent.PopBackStack)
        }
    }

    private fun getChatMessages() {
        val chat = screenState.chat
        if (chat != null)
            viewModelScope.launch(Dispatchers.IO) {
                cases.getChatMessages(chat.id, ::startConnection, ::onErrorEvent, screenState)
            }
    }

    private fun startConnection() {
        val chat = screenState.chat
        if (chat != null)
            viewModelScope.launch(Dispatchers.IO) {
                cases.connectToChatMessages(
                    chat.id,
                    viewModelScope,
                    ::onReceive,
                    ::onClose,
                    ::onError
                )
            }
    }

    private fun onReceive(message: Message) {
        if (!screenState.isFirstLoading) screenState.addMessage(message)
    }

    private fun onClose(reason: WSCloseReason) {
        when (reason) {
            WSCloseReason.USER_NOT_FOUND -> TODO()
            WSCloseReason.UNAUTHORIZED_CHAT_ACCESS -> TODO()
            WSCloseReason.CHAT_NOT_FOUND -> TODO()
            WSCloseReason.SENT_ITEM_INVALID -> TODO()
            else -> Unit
        }
    }

    private fun onError(throwable: Throwable) {
        when (throwable) {
            is NetworkResponseException -> TODO()
            is ConnectionNotEstablishedException -> TODO()
            else -> TODO()
        }
    }

    fun checkNextItems() {
        getChatMessages()
    }

    fun checkOwner(message: Message): Boolean = screenState.user?.id != message.userId

    private fun onErrorEvent(event: ErrorEvent) {
        when (event) {
            ErrorEvent.LOGOUT -> logout()
            ErrorEvent.EXIT -> sendUiEvent(MessageListUiEvent.PopBackStack)
        }
    }

    private fun onSendClicked() {
        viewModelScope.launch {
            cases.sendMessage(
                content = message,
                screenState = screenState,
                onEvent = ::onSendEvent
            )
        }
    }

    private fun onSendEvent(event: SendChatEvent) {
        when (event) {
            is SendChatEvent.ChatCreated -> startConnection()
            SendChatEvent.CleanMessage -> message = ""
        }
    }

    private fun onUserClicked() {

    }

    private fun logout() {
        cases.logout()
        sendUiEvent(MessageListUiEvent.Logout)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCleared() {
        GlobalScope.launch(Dispatchers.IO) {
            cases.closeChatMessagesConnection()
        }
        super.onCleared()
    }
}
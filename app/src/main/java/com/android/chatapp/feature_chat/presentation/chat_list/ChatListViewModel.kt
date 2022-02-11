package com.android.chatapp.feature_chat.presentation.chat_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.chatapp.core.data.util.ConnectionNotEstablishedException
import com.android.chatapp.core.data.util.NetworkResponseException
import com.android.chatapp.core.data.util.WSCloseReason
import com.android.chatapp.feature_chat.domain.model.Chat
import com.android.chatapp.feature_chat.domain.use_case.ChatListUseCases
import com.android.chatapp.feature_chat.presentation.chat_list.components.ChatItemEvent
import com.android.chatapp.feature_chat.presentation.util.ErrorEvent
import com.android.chatapp.feature_chat.presentation.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(private val cases: ChatListUseCases) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Chat>>(UiState.Loading)
    val uiState get() = _uiState.asStateFlow()

    private val _uiEvent = Channel<ChatListUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var userSearchKeywords by mutableStateOf("")
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            cases.getChats(::startConnection, ::onErrorEvent).collect {
                _uiState.value = it
            }
        }
    }

    private fun sendUiEvent(event: ChatListUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    fun onEvent(event: ChatListEvent) {
        when (event) {
            ChatListEvent.Logout -> onLogoutClicked()
            is ChatListEvent.ChatItemClicked -> onChatItemClicked(event.event)
            is ChatListEvent.UserSearchValueChanged -> onUserSearchValueChanged(event.value)
            ChatListEvent.UserSearchButtonClicked -> onSearchButtonClicked()
        }
    }

    private fun onSearchButtonClicked() {
        if (userSearchKeywords.isNotBlank())
            sendUiEvent(ChatListUiEvent.NavigateToSearchProfiles(userSearchKeywords))
    }

    private fun onUserSearchValueChanged(value: String) {
        userSearchKeywords = value
    }

    private fun startConnection() {
        viewModelScope.launch(Dispatchers.IO) {
            cases.connectToChats(viewModelScope, ::onReceive, ::onClose, ::onError)
        }
    }

    /**
     * @property chat.user.profile.media need to check if its id == oldChat media id
     * because my server side generate new link to media on requesting for user
     * which make user image reload again, which lead to  bad experience
     */
    private fun onReceive(chat: Chat) {
        var newChat = chat
        _uiState.value.apply {
            when (this) {
                is UiState.Loaded ->
                    _uiState.value = UiState.Loaded(mutableListOf<Chat>().apply {
                        addAll(items)
                        val index = indexOfFirst { it.id == chat.id }
                        if (index != -1) {
                            val oldChat = get(index)
                            if (oldChat.user.profile.image?.id == chat.user.profile.image?.id)
                                newChat = chat.copy(
                                    user = chat.user.copy(
                                        profile = chat.user.profile.copy(image = oldChat.user.profile.image)
                                    )
                                )
                            removeAt(index)
                        }
                        add(0, newChat)
                    })

                UiState.Empty -> _uiState.value = UiState.Loaded(listOf(chat))
                else -> Unit
            }
        }
    }

    private fun onClose(reason: WSCloseReason) {
        when (reason) {
            WSCloseReason.USER_NOT_FOUND -> TODO()
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


    private fun onErrorEvent(event: ErrorEvent) {
        when (event) {
            ErrorEvent.LOGOUT -> onLogoutClicked()
            ErrorEvent.EXIT -> Unit
        }
    }

    private fun onLogoutClicked() {
        cases.logout()
        sendUiEvent(ChatListUiEvent.Logout)
    }

    private fun onChatItemClicked(event: ChatItemEvent) {
        sendUiEvent(
            when (event) {
                is ChatItemEvent.ChatClicked -> ChatListUiEvent.NavigateToChat(event.chat)
                is ChatItemEvent.UserClicked -> ChatListUiEvent.NavigateToUserProfile(event.chat.user)
            }
        )
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCleared() {
        GlobalScope.launch(Dispatchers.IO) {
            cases.closeChatsConnection()
        }
        super.onCleared()
    }
}
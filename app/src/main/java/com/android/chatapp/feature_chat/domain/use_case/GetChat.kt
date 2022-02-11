package com.android.chatapp.feature_chat.domain.use_case

import com.android.chatapp.core.data.util.ApiException
import com.android.chatapp.core.data.util.NetworkResponseException
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_authentication.data.util.AuthApiExceptions
import com.android.chatapp.feature_chat.data.util.ApiExceptions
import com.android.chatapp.feature_chat.domain.model.Chat
import com.android.chatapp.feature_chat.domain.repository.ChatRepository
import com.android.chatapp.feature_chat.presentation.message_list.ScreenState
import com.android.chatapp.feature_chat.presentation.util.ErrorEvent
import com.android.chatapp.feature_chat.presentation.util.UiError
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import javax.inject.Inject

class GetChat @Inject constructor(private val repository: ChatRepository) {

    /**
     * @param cid: chat id
     * @param oid: id of other user in the chat
     **/
    suspend operator fun invoke(
        cid: Long?,
        oid: Long?,
        screenState: ScreenState,
        getMessages: () -> Unit,
        onErrorEvent: (ErrorEvent) -> Unit,
    ) {
        repository.getChat(cid, oid)
            .filter { result -> filterResult(result, onErrorEvent) }
            .collect { result -> onChatResult(result, screenState, getMessages) }
    }

    private fun onChatResult(
        result: Resource<Chat, ApiException>,
        screenState: ScreenState,
        getMessages: () -> Unit
    ) {
        when (result) {
            is Resource.Success -> {
                screenState.chat = result.data
                screenState.stopLoading
                getMessages()
            }
            is Resource.Failure -> {
                result.errors.forEach {
                    when (it) {
                        ApiExceptions.noChatMatched -> {
                            screenState.stopLoading
                            screenState.stopLoadAbility
                        }
                        else -> screenState.setError(UiError.GENERAL)
                    }
                }
            }
            is Resource.Error -> {
                screenState.setError(
                    if (result.throwable is NetworkResponseException) UiError.NETWORK
                    else UiError.GENERAL
                )
            }
            is Resource.Loading -> screenState.startLoading
        }
    }

    private fun filterResult(
        result: Resource<Chat, ApiException>,
        onErrorEvent: (ErrorEvent) -> Unit
    ): Boolean {
        if (result is Resource.Failure) {
            result.errors.forEach {
                when (it) {
                    AuthApiExceptions.notAuthenticatedToken,
                    AuthApiExceptions.profileNotFound,
                    AuthApiExceptions.userDisabled,
                    AuthApiExceptions.notAuthenticated -> {
                        onErrorEvent(ErrorEvent.LOGOUT)
                        return false
                    }
                    ApiExceptions.noChatRequested,
                    ApiExceptions.noChatWithId,
                    ApiExceptions.chattingSelfNotPermitted,
                    ApiExceptions.noChatUserWithId -> {
                        onErrorEvent(ErrorEvent.EXIT)
                        return false
                    }

                }
            }
        }
        return true
    }
}
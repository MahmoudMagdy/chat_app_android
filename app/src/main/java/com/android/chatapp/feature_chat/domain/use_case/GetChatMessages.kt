package com.android.chatapp.feature_chat.domain.use_case

import com.android.chatapp.core.data.util.*
import com.android.chatapp.feature_authentication.data.util.AuthApiExceptions
import com.android.chatapp.feature_chat.data.util.ApiExceptions
import com.android.chatapp.feature_chat.domain.model.Message
import com.android.chatapp.feature_chat.domain.repository.ChatRepository
import com.android.chatapp.feature_chat.presentation.message_list.ScreenState
import com.android.chatapp.feature_chat.presentation.util.ErrorEvent
import com.android.chatapp.feature_chat.presentation.util.UiError
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import javax.inject.Inject

class GetChatMessages @Inject constructor(private val repository: ChatRepository) {

    suspend operator fun invoke(
        id: Long,
        startConnection: () -> Unit,
        onErrorEvent: (ErrorEvent) -> Unit,
        screenState: ScreenState
    ) {
        if (!screenState.loadAbility || screenState.loading) return
        repository.getChatMessages(id, screenState.page, screenState.latestMessageId)
            .filter { result -> filterResult(result, screenState, onErrorEvent) }
            .collect { result -> onResult(result, screenState, startConnection) }
    }


    /**
     * ***Note***
     * [Resource.Failure] always mapped to [UiError.GENERAL] as all the known errors will be
     * filtered and forwarded as Logical Error not [UiError].
     **/
    private fun onResult(
        result: Resource<PaginationResult<Message>, ApiException>,
        screenState: ScreenState,
        startConnection: () -> Unit
    ) {
        when (result) {
            is Resource.Error -> onError(result.throwable, screenState, result.items())
            is Resource.Failure -> screenState.setError(UiError.GENERAL, result.items())
            is Resource.Loading -> screenState.startLoading(result.items())
            is Resource.Success -> {
                if (screenState.isFirstPage) startConnection()
                screenState.setSuccessData(result.data)
            }
        }
    }

    private fun filterResult(
        result: Resource<PaginationResult<Message>, ApiException>,
        screenState: ScreenState,
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
                    ApiExceptions.invalidPage ->
                        if (screenState.isFirstPage) {
                            onErrorEvent(ErrorEvent.EXIT)
                            return false
                        }
                    ApiExceptions.notChatMember, ApiExceptions.noChatWithId -> {
                        onErrorEvent(ErrorEvent.EXIT)
                        return false
                    }

                }
            }
        }
        return true
    }

    private fun onError(throwable: Throwable, screenState: ScreenState, items: List<Message>?) {
        screenState.setError(
            if (throwable is NetworkResponseException) UiError.NETWORK
            else UiError.GENERAL,
            items
        )
    }
}
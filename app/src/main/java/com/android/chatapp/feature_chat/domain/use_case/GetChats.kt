package com.android.chatapp.feature_chat.domain.use_case

import com.android.chatapp.core.data.util.ApiException
import com.android.chatapp.core.data.util.NetworkResponseException
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_authentication.data.util.AuthApiExceptions
import com.android.chatapp.feature_chat.domain.model.Chat
import com.android.chatapp.feature_chat.domain.repository.ChatRepository
import com.android.chatapp.feature_chat.presentation.util.ErrorEvent
import com.android.chatapp.feature_chat.presentation.util.UiError
import com.android.chatapp.feature_chat.presentation.util.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetChats @Inject constructor(private val repository: ChatRepository) {

    operator fun invoke(
        startConnection: () -> Unit,
        onErrorEvent: (ErrorEvent) -> Unit
    ): Flow<UiState<Chat>> =
        repository.getChats()
            .filter { filterUiStats(it, onErrorEvent) }
            .map { res ->
                when (res) {
                    is Resource.Error -> UiState.Error(
                        if (res.throwable is NetworkResponseException) UiError.NETWORK
                        else UiError.GENERAL, res.data
                    )
                    is Resource.Failure -> UiState.Error(UiError.GENERAL, res.data)
                    is Resource.Loading ->
                        if (res.data != null) UiState.Loaded(res.data)
                        else UiState.Loading
                    is Resource.Success -> {
                        startConnection()
                        if (res.data.isEmpty()) UiState.Empty
                        else UiState.Loaded(res.data)
                    }
                }
            }

    private fun filterUiStats(
        result: Resource<List<Chat>, ApiException>,
        onLogicalError: (ErrorEvent) -> Unit
    ): Boolean {
        if (result is Resource.Failure) {
            result.errors.forEach {
                when (it) {
                    AuthApiExceptions.notAuthenticatedToken,
                    AuthApiExceptions.userDisabled,
                    AuthApiExceptions.profileNotFound,
                    AuthApiExceptions.notAuthenticated -> {
                        onLogicalError(ErrorEvent.LOGOUT)
                        return false
                    }
                }
            }
        }
        return true
    }

}
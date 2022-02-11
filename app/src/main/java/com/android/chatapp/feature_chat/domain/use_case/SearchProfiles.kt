package com.android.chatapp.feature_chat.domain.use_case

import com.android.chatapp.core.data.util.ApiException
import com.android.chatapp.core.data.util.NetworkResponseException
import com.android.chatapp.core.data.util.PaginationResult
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_authentication.data.util.AuthApiExceptions
import com.android.chatapp.feature_authentication.domain.model.User
import com.android.chatapp.feature_authentication.domain.repository.AuthRepository
import com.android.chatapp.feature_chat.data.util.ApiExceptions
import com.android.chatapp.feature_chat.presentation.util.ErrorEvent
import com.android.chatapp.feature_chat.presentation.util.PaginationUiState
import com.android.chatapp.feature_chat.presentation.util.UiError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchProfiles @Inject constructor(private val repository: AuthRepository) {
    var latestResponse: PaginationResult<User>? = null
        private set
    private val users: MutableList<User> = mutableListOf()
    private var keyword: String? = null
    operator fun invoke(
        keyword: String,
        onErrorEvent: (ErrorEvent) -> Unit,
    ): Flow<PaginationUiState<User>>? {
        val cleanKeyword = keyword.trim()
        if (this.keyword != cleanKeyword) reset(cleanKeyword)
        val latestResponse = latestResponse
        return if (latestResponse == null || latestResponse.next != null)
            repository.searchUsers(keyword, latestResponse?.next ?: 1)
                .filter { filterUiStats(it, onErrorEvent) }
                .map { res ->
                    if (latestResponse == null) mapFirstRequest(res)
                    else mapNextRequest(res)
                }
        else null
    }

    private fun mapFirstRequest(result: Resource<PaginationResult<User>, ApiException>) =
        when (result) {
            is Resource.Error -> onError(result.throwable, null)
            is Resource.Failure ->
                if (result.errors[0] == ApiExceptions.invalidPage) PaginationUiState.Empty
                else PaginationUiState.Error(UiError.GENERAL)
            is Resource.Loading -> PaginationUiState.Loading
            is Resource.Success -> {
                latestResponse = result.data
                if (result.data.results.isEmpty()) PaginationUiState.Empty
                else {
                    users.addAll(result.data.results)
                    PaginationUiState.Loaded(users)
                }
            }
        }

    private fun mapNextRequest(res: Resource<PaginationResult<User>, ApiException>) =
        when (res) {
            is Resource.Error -> onError(res.throwable, items = users)
            is Resource.Failure -> PaginationUiState.Error(UiError.GENERAL, items = users)
            is Resource.Loading -> PaginationUiState.NextLoading(users)
            is Resource.Success -> {
                latestResponse = res.data
                if (res.data.results.isNotEmpty()) users.addAll(res.data.results)
                PaginationUiState.Loaded(users)
            }
        }

    private fun onError(throwable: Throwable, items: List<User>?): PaginationUiState<User> {
        return PaginationUiState.Error(
            if (throwable is NetworkResponseException) UiError.NETWORK
            else UiError.GENERAL, items
        )
    }

    private fun filterUiStats(
        result: Resource<PaginationResult<User>, ApiException>,
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
                }
            }
        }
        return true
    }

    private fun reset(keyword: String) {
        this.keyword = keyword
        latestResponse = null
        users.clear()
    }

}
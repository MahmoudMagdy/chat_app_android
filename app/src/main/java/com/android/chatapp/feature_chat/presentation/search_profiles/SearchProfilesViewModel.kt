package com.android.chatapp.feature_chat.presentation.search_profiles

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.chatapp.feature_authentication.domain.model.User
import com.android.chatapp.feature_chat.domain.use_case.ProfileSearchUseCases
import com.android.chatapp.feature_chat.presentation.search_profiles.components.UserItemEvent
import com.android.chatapp.feature_chat.presentation.util.ErrorEvent
import com.android.chatapp.feature_chat.presentation.util.PaginationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchProfilesViewModel @Inject constructor(
    private val cases: ProfileSearchUseCases,
    savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    private val _uiState = MutableStateFlow<PaginationUiState<User>>(PaginationUiState.Loading)
    val uiState get() = _uiState.asStateFlow()

    private val _uiEvent = Channel<SearchProfilesUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _userSearchKeywords = MutableStateFlow(savedStateHandle.get<String>(KEYWORD) ?: "")
    val userSearchKeywords = _userSearchKeywords.asStateFlow()

    var searchJob: Job? = null

    init {
        search()
    }

    private fun search() {
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            cases.searchProfiles(userSearchKeywords.value, ::onErrorEvent)?.collect {
                _uiState.value = it
            }
        }
    }

    private fun sendUiEvent(event: SearchProfilesUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    fun onEvent(event: SearchProfilesEvent) {
        when (event) {
            is SearchProfilesEvent.UserItemClicked -> onChatItemClicked(event.event)
            is SearchProfilesEvent.UserSearchValueChanged -> onUserSearchValueChanged(event.value)
            SearchProfilesEvent.UserSearchButtonClicked -> onSearchClicked()
        }
    }

    private fun onSearchClicked() {
        search()
    }

    fun checkNextItems() {
        _uiState.value.also {
            if (it !is PaginationUiState.Loading && it !is PaginationUiState.NextLoading) search()
        }
    }

    private fun onUserSearchValueChanged(value: String) {
        _userSearchKeywords.value = value
    }

    private fun onErrorEvent(event: ErrorEvent) {
        when (event) {
            ErrorEvent.LOGOUT -> logout()
            ErrorEvent.EXIT -> Unit
        }
    }

    private fun logout() {
        cases.logout()
        sendUiEvent(SearchProfilesUiEvent.Logout)
    }

    private fun onChatItemClicked(event: UserItemEvent) {
        sendUiEvent(
            when (event) {
                is UserItemEvent.ChatClicked -> SearchProfilesUiEvent.NavigateToChat(event.user)
                is UserItemEvent.UserClicked -> SearchProfilesUiEvent.NavigateToUserProfile(event.user.profile)
            }
        )
    }

    override fun onCleared() {
        searchJob?.cancel()
        super.onCleared()
    }
}
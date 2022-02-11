package com.android.chatapp.feature_chat.presentation.search_profiles

import com.android.chatapp.feature_authentication.domain.model.Profile
import com.android.chatapp.feature_authentication.domain.model.User
import com.android.chatapp.feature_chat.domain.model.Chat

sealed class SearchProfilesUiEvent {
    data class NavigateToChat(val user: User) : SearchProfilesUiEvent()
    data class NavigateToUserProfile(val profile: Profile) : SearchProfilesUiEvent()
    object Logout : SearchProfilesUiEvent()
}
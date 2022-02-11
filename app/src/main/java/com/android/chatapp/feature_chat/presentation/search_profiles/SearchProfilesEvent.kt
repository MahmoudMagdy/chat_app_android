package com.android.chatapp.feature_chat.presentation.search_profiles

import com.android.chatapp.feature_chat.presentation.search_profiles.components.UserItemEvent

sealed class SearchProfilesEvent {
    data class UserItemClicked(val event: UserItemEvent) : SearchProfilesEvent()
    data class UserSearchValueChanged(val value: String) : SearchProfilesEvent()
    object UserSearchButtonClicked : SearchProfilesEvent()
}
package com.android.chatapp.feature_chat.presentation.util

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    object Empty : UiState<Nothing>()
    data class  Loaded<out T>(val items: List<T>) : UiState<T>()
    data class Error<out T>(val error: UiError, val items: List<T>? = null) :
        UiState<T>()
}

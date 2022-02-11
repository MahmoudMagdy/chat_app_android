package com.android.chatapp.feature_chat.presentation.util

sealed class PaginationUiState<out T> {
    object Loading : PaginationUiState<Nothing>()
    object Empty : PaginationUiState<Nothing>()
    data class NextLoading<out T>(val items: List<T>) : PaginationUiState<T>()
    data class Loaded<out T>(val items: List<T>) : PaginationUiState<T>()
    data class Error<out T>(val error: UiError, val items: List<T>? = null) :
        PaginationUiState<T>()
}

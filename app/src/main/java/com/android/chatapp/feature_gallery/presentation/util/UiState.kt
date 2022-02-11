package com.android.chatapp.feature_gallery.presentation.util

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    object Empty : UiState<Nothing>()
    data class Loaded<out T>(val items: List<T>, val gallery: Gallery) : UiState<T>()
    data class Error(val exception: Throwable) : UiState<Nothing>()
}

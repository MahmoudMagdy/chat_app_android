package com.android.chatapp.feature_authentication.presentation.login

import androidx.annotation.StringRes
import androidx.compose.material.SnackbarDuration
import com.android.chatapp.feature_authentication.presentation.util.Screen

sealed class UiEvent {
    object LoginCompleted : UiEvent()
    data class Navigate(val screen: Screen) : UiEvent()
    data class ShowSnackbar(
        @StringRes val message: Int,
        val duration: SnackbarDuration,
        @StringRes val actionLabel: Int? = null,
        val action: LoginEvent? = null
    ) : UiEvent()
}

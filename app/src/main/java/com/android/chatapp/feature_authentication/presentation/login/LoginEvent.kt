package com.android.chatapp.feature_authentication.presentation.login

import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.ui.text.input.ImeAction
import com.android.chatapp.feature_dialog.presentation.message.MessageDialogEvent
import com.android.chatapp.feature_dialog.presentation.progress.ProgressDialogEvent

sealed class LoginEvent {
    data class PasswordImeAction(val actionScope: KeyboardActionScope, val imeAction: ImeAction) : LoginEvent()
    data class ConfirmPasswordImeAction(val imeAction: ImeAction) : LoginEvent()
    data class OnMessageDialogEvent(val event: MessageDialogEvent) : LoginEvent()
    data class OnProgressDialogEvent(val event: ProgressDialogEvent) : LoginEvent()
    object LoginButtonClicked : LoginEvent()
    object ForgetPasswordClickedClicked : LoginEvent()
    object SwitchLoginUiClicked : LoginEvent()
    object DisplayRegisterUi : LoginEvent()
}

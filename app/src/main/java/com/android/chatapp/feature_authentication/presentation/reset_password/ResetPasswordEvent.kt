package com.android.chatapp.feature_authentication.presentation.reset_password

import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.ui.text.input.ImeAction
import com.android.chatapp.feature_dialog.presentation.message.MessageDialogEvent

sealed class ResetPasswordEvent {
    data class EmailImeAction(val actionScope: KeyboardActionScope, val imeAction: ImeAction) : ResetPasswordEvent()
    data class OnMessageDialogEvent(val event: MessageDialogEvent) : ResetPasswordEvent()
    object ResetPasswordClicked : ResetPasswordEvent()
    object BackButtonClicked : ResetPasswordEvent()
}

package com.android.chatapp.feature_dialog.presentation.message

sealed class MessageDialogEvent {
    object DismissRequested : MessageDialogEvent()
    data class ActionClicked(val action: Int) : MessageDialogEvent()
}

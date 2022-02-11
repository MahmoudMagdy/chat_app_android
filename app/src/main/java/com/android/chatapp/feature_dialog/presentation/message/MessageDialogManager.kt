package com.android.chatapp.feature_dialog.presentation.message

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.android.chatapp.R
import com.android.chatapp.feature_dialog.presentation.util.DialogManager

class MessageDialogManager(onEvent: ((MessageDialogEvent) -> Unit)? = null) : DialogManager {
    var state by mutableStateOf<MessageDialogState?>(null)
        private set

    val generalError
        get() = show(
            R.string.gen_error_msg_title,
            R.string.gen_error_msg_content
        )

    val onEvent = onEvent ?: { close }

    val networkError
        get() = show(
            R.string.gen_network_error_title,
            R.string.gen_network_error_content
        )

    val close get() = run { if (state != null) state = null }

    override fun show(@StringRes title: Int, @StringRes content: Int) {
        state = MessageDialogState(title, content)
    }

    fun show(
        @StringRes title: Int,
        @StringRes content: Int,
        @StringRes actionTitle: Int,
        action: Int
    ) {
        state = MessageDialogState(title, content, actionTitle, action)
    }
}

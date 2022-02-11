package com.android.chatapp.feature_dialog.presentation.image_selector

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.android.chatapp.R
import com.android.chatapp.feature_dialog.presentation.util.DialogManager

class ImageSelectorDialogManager(onEvent: ((ImageSelectorEvent) -> Unit)? = null) :
    DialogManager {
    var state by mutableStateOf<ImageSelectorDialogState?>(null)
        private set

    val onEvent = onEvent ?: { close }

    val close get() = run { if (state != null) state = null }

    fun show(@StringRes title: Int) = show(title, R.string.img_selector_cancel_title)

    override fun show(@StringRes title: Int, @StringRes content: Int) {
        state = ImageSelectorDialogState(title, content)
    }
}

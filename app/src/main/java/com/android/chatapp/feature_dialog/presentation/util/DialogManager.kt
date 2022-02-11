package com.android.chatapp.feature_dialog.presentation.util

import androidx.annotation.StringRes
import com.android.chatapp.feature_dialog.domain.model.DialogData

interface DialogManager {
    fun show(@StringRes title: Int, @StringRes content: Int)
}

infix fun DialogData.to(dialog: DialogManager) = dialog.show(title, content)

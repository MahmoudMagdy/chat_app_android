package com.android.chatapp.feature_dialog.domain.model

import androidx.annotation.StringRes

data class DialogData(@StringRes val title: Int, @StringRes val content: Int)

infix fun Int.attach(@StringRes content: Int) = DialogData(this, content)

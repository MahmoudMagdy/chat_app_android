package com.android.chatapp.feature_chat.presentation.util

import androidx.annotation.StringRes
import com.android.chatapp.R

enum class UiError(@StringRes val title: Int, @StringRes val content: Int) {
    GENERAL(R.string.gen_error_msg_title, R.string.gen_error_msg_content),
    NETWORK(R.string.gen_network_error_title, R.string.gen_network_error_content)
}

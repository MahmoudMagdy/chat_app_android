package com.android.chatapp.feature_authentication.domain.util

import androidx.annotation.StringRes

sealed class LoginFieldError(@StringRes val msg: Int) {
    class Email(@StringRes msg: Int) : LoginFieldError(msg)
    class Password(@StringRes msg: Int) : LoginFieldError(msg)
    class ConfirmPassword(@StringRes msg: Int) : LoginFieldError(msg)
    class Username(@StringRes msg: Int) : LoginFieldError(msg)
    class FirstName(@StringRes msg: Int) : LoginFieldError(msg)
    class LastName(@StringRes msg: Int) : LoginFieldError(msg)
    class Date(@StringRes msg: Int) : LoginFieldError(msg)
}


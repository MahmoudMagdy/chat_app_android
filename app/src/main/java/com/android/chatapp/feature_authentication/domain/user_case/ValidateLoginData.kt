package com.android.chatapp.feature_authentication.domain.user_case

import com.android.chatapp.R
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_authentication.data.remote.dto.LoginRequest
import com.android.chatapp.feature_authentication.domain.util.LoginFieldError
import com.android.chatapp.feature_authentication.domain.util.validEmail
import com.android.chatapp.feature_authentication.domain.util.validPassword
import javax.inject.Inject

class ValidateLoginData @Inject constructor() {

    operator fun invoke(email: String, password: String): Resource<LoginRequest, LoginFieldError> {
        val cleanEmail = email.trim().lowercase()
        mutableListOf<LoginFieldError>().apply {
            if (cleanEmail.isBlank() || !cleanEmail.validEmail)
                add(LoginFieldError.Email(R.string.login_scr_invalid_email_msg))
            if (password.isEmpty())
                add(LoginFieldError.Password(R.string.login_scr_empty_password_msg))
            else if (!password.validPassword)
                add(LoginFieldError.Password(R.string.login_scr_incorrect_password_msg))
            if (isNotEmpty())
                return@invoke Resource.Failure(this)
        }
        return Resource.Success(LoginRequest(cleanEmail, password))
    }
}
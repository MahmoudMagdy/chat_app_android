package com.android.chatapp.feature_authentication.domain.user_case

import com.android.chatapp.R
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_authentication.data.remote.dto.RegisterRequest
import com.android.chatapp.feature_authentication.domain.util.LoginFieldError
import com.android.chatapp.feature_authentication.domain.util.validEmail
import com.android.chatapp.feature_authentication.domain.util.validPassword
import com.android.chatapp.feature_authentication.domain.util.validUsername
import javax.inject.Inject

class ValidateRegisterData @Inject constructor() {

    operator fun invoke(email: String, username: String, password: String, confirmPassword: String): Resource<RegisterRequest, LoginFieldError> {
        val cleanEmail = email.trim().lowercase()
        val cleanUsername = username.trim().lowercase()
        mutableListOf<LoginFieldError>().apply {
            if (cleanEmail.isBlank() || !cleanEmail.validEmail)
                add(LoginFieldError.Email(R.string.login_scr_invalid_email_msg))
            if (cleanUsername.isBlank() || !cleanUsername.validUsername)
                add(LoginFieldError.Username(R.string.login_scr_invalid_username_msg))
            if (password.isEmpty())
                add(LoginFieldError.Password(R.string.login_scr_empty_password_msg))
            else if (!password.validPassword)
                add(LoginFieldError.Password(R.string.login_scr_weak_password_msg))
            if (!password.equals(confirmPassword, false))
                add(LoginFieldError.ConfirmPassword(R.string.login_scr_confirm_password_msg))
            if (isNotEmpty())
                return@invoke Resource.Failure(this)
        }
        return Resource.Success(RegisterRequest(cleanUsername, cleanEmail, password))
    }
}
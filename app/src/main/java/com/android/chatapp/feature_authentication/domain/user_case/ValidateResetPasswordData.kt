package com.android.chatapp.feature_authentication.domain.user_case

import com.android.chatapp.R
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_authentication.domain.util.LoginFieldError
import com.android.chatapp.feature_authentication.domain.util.validEmail
import javax.inject.Inject

class ValidateResetPasswordData @Inject constructor() {

    operator fun invoke(email: String): Resource<String, LoginFieldError> {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank() || !cleanEmail.validEmail)
            return Resource.Failure(listOf(LoginFieldError.Email(R.string.login_scr_invalid_email_msg)))
        return Resource.Success(cleanEmail)
    }
}
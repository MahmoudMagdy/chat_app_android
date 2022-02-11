package com.android.chatapp.feature_authentication.domain.user_case

import com.android.chatapp.feature_authentication.data.remote.dto.ResetPasswordRequest
import com.android.chatapp.feature_authentication.data.util.EmailResetDateNotFoundException
import com.android.chatapp.feature_authentication.domain.repository.AuthRepository
import javax.inject.Inject

class CheckResetPasswordLimit @Inject constructor(private val repository: AuthRepository) {

    @Throws(ResetPasswordExceededLimits::class)
    operator fun invoke(email: String): ResetPasswordRequest {
        val exceededLimits: Boolean
        try {
            exceededLimits = repository.isEmailDateExceededTimeLimit(email, RESET_TIME_LIMIT)
        } catch (ex: EmailResetDateNotFoundException) {
            return ResetPasswordRequest(email)
        }
        if (exceededLimits)
            return ResetPasswordRequest(email)
        else throw  ResetPasswordExceededLimits()
    }

    companion object {
        private const val RESET_TIME_LIMIT = 120000L
    }
}

class ResetPasswordExceededLimits : Exception()
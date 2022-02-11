package com.android.chatapp.feature_authentication.data.provider.reset_password

interface ResetPasswordProvider {
    fun setEmailDate(email: String)
    fun getEmailDate(email: String): Long
    fun isEmailDateExceededTimeLimit(email: String, mills: Long): Boolean
    fun isEmailDateExists(email: String): Boolean
}
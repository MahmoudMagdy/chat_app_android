package com.android.chatapp.feature_authentication.data.provider.reset_password

import android.content.Context
import android.content.SharedPreferences
import com.android.chatapp.feature_authentication.data.util.EmailResetDateNotFoundException
import java.util.*
import kotlin.math.absoluteValue


class ResetPasswordProviderImpl(context: Context) : ResetPasswordProvider {
    companion object {
        private const val PREFERENCE_NAME = "reset_preference"
        private const val DATE_DEFAULT_VALUE = -1L
    }

    private val appContext = context.applicationContext
    private val preferences: SharedPreferences = appContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    override fun setEmailDate(email: String) {
        preferences.edit().putLong(email, Date().time).apply()
    }

    override fun getEmailDate(email: String): Long = preferences.getLong(email, DATE_DEFAULT_VALUE)

    @Throws(EmailResetDateNotFoundException::class)
    override fun isEmailDateExceededTimeLimit(email: String, mills: Long): Boolean =
        if (isEmailDateExists(email)) {
            getEmailDate(email).let { time ->
                if (time != DATE_DEFAULT_VALUE) (Date().time - time).absoluteValue >= mills
                else throw  EmailResetDateNotFoundException()
            }

        } else throw  EmailResetDateNotFoundException()

    override fun isEmailDateExists(email: String): Boolean = preferences.contains(email)
}
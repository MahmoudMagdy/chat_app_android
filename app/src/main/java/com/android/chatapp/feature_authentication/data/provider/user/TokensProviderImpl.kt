package com.android.chatapp.feature_authentication.data.provider.user

import android.content.Context
import android.content.SharedPreferences
import com.android.chatapp.feature_authentication.data.remote.dto.UserResponse
import io.ktor.client.features.auth.providers.*


class TokensProviderImpl(context: Context) : TokensProvider {
    companion object {
        private const val PREFERENCE_NAME = "user_preference"
        private const val ID_TAG = "id"
        private const val ACCESS_TOKEN_TAG = "access_token"
        private const val REFRESH_TOKEN_TAG = "refresh_token"
    }

    private val appContext = context.applicationContext
    private val preferences: SharedPreferences =
        appContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    override val uid: Long get() = preferences.getLong(ID_TAG, -1)
    override val accessToken: String? get() = preferences.getString(ACCESS_TOKEN_TAG, null)
    override val refreshToken: String? get() = preferences.getString(REFRESH_TOKEN_TAG, null)
    override val bearerTokens: BearerTokens?
        get() = accessToken?.let {
            BearerTokens(
                it,
                refreshToken.toString()
            )
        }

    override fun setTokens(userResponse: UserResponse) {
        userResponse.tokens?.apply {
            preferences.edit()
                .putLong(ID_TAG, userResponse.id)
                .putString(ACCESS_TOKEN_TAG, access)
                .putString(REFRESH_TOKEN_TAG, refresh)
                .apply()
        }
    }

    override fun updateTokens(bearerTokens: BearerTokens) {
        preferences.edit()
            .putString(ACCESS_TOKEN_TAG, bearerTokens.accessToken)
            .putString(REFRESH_TOKEN_TAG, bearerTokens.refreshToken)
            .apply()
    }

    override fun logout() {
        if (accessToken != null)
            preferences.edit()
                .remove(ID_TAG)
                .remove(ACCESS_TOKEN_TAG)
                .remove(REFRESH_TOKEN_TAG)
                .apply()
    }
}
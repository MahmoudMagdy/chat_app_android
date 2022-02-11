package com.android.chatapp.feature_authentication.data.provider.user

import com.android.chatapp.feature_authentication.data.remote.dto.UserResponse
import io.ktor.client.features.auth.providers.*

interface TokensProvider {
    val uid: Long
    val accessToken: String?
    val refreshToken: String?
    val bearerTokens: BearerTokens?
    fun setTokens(userResponse: UserResponse)
    fun updateTokens(bearerTokens: BearerTokens)
    fun logout()
}
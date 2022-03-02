package com.android.chatapp.feature_authentication.data.provider.token

import com.android.chatapp.TokenPreferences
import io.ktor.client.features.auth.providers.*

interface TokenProvider {
    suspend fun bearerTokens(): BearerTokens?
    suspend fun uid(): Long?
    suspend fun set(token: TokenPreferences): TokenPreferences
    suspend fun update(bearerTokens: BearerTokens): TokenPreferences
    suspend fun logout(): TokenPreferences
}
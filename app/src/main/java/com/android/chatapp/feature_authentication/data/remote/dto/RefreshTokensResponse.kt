package com.android.chatapp.feature_authentication.data.remote.dto

import io.ktor.client.features.auth.providers.*
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokensResponse(val access: String) {
    fun bearerTokens(refresh: String) = BearerTokens(access, refresh)
}

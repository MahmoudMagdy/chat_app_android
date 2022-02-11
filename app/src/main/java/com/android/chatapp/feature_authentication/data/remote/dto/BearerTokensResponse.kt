package com.android.chatapp.feature_authentication.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class BearerTokensResponse(val access: String, val refresh: String)
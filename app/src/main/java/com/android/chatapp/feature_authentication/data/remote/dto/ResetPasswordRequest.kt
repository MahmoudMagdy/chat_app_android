package com.android.chatapp.feature_authentication.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequest(val email: String)


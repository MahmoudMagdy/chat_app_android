package com.android.chatapp.feature_authentication.data.remote.dto

import com.android.chatapp.feature_authentication.domain.model.ProfileMediaType
import kotlinx.serialization.Serializable

@Serializable
data class ProfileUploadUrlRequest(
    val type: ProfileMediaType,
    val extension: String,
    val size: Long,
)

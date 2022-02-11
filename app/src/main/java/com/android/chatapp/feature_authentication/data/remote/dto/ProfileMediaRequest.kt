package com.android.chatapp.feature_authentication.data.remote.dto

import com.android.chatapp.feature_authentication.domain.model.ProfileMediaType
import kotlinx.serialization.Serializable

@Serializable
data class ProfileMediaRequest(
    val media: String,
    val name: String,
    val type: ProfileMediaType,
    val extension: String,
    val size: Long,
)

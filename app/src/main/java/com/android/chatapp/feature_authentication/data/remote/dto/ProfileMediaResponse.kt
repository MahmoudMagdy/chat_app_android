package com.android.chatapp.feature_authentication.data.remote.dto

import com.android.chatapp.feature_authentication.data.local.entity.ProfileMediaEntity
import com.android.chatapp.feature_authentication.domain.model.ProfileMedia
import com.android.chatapp.feature_authentication.domain.model.ProfileMediaType
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileMediaResponse(
    val id: Long,
    val media: String,
    val name: String,
    val type: ProfileMediaType,
    val extension: String,
    val size: Long,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("profile_id")
    val profileId: Long
)

val ProfileMediaResponse.entity
    get() =
        ProfileMediaEntity(id, media, name, type, extension, size, createdAt, profileId)
val ProfileMediaResponse.model get() = ProfileMedia(id, media)



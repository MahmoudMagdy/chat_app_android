package com.android.chatapp.feature_authentication.data.remote.dto

import com.android.chatapp.feature_authentication.data.local.entity.UserEntity
import com.android.chatapp.feature_authentication.domain.model.User
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Long,
    val email: String,
    val username: String,
    @SerialName("is_verified")
    val isVerified: Boolean,
    @SerialName("is_active")
    val isActive: Boolean,
    @SerialName("is_staff")
    val isStaff: Boolean,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("updated_at")
    val updatedAt: Instant,
    @SerialName("last_login")
    val lastLogin: Instant,
    val tokens: BearerTokensResponse? = null,
    val profile: ProfileResponse? = null,
    val session: SessionResponse? = null
)

val UserResponse.entity
    get() = UserEntity(
        id, email, username, isVerified, isActive,
        isStaff, createdAt, updatedAt, lastLogin, false
    )

val UserResponse.loggedEntity
    get() = UserEntity(
        id, email, username, isVerified, isActive,
        isStaff, createdAt, updatedAt, lastLogin, true
    )

val UserResponse.profileEntity get() = profile?.entity
val UserResponse.mediaEntity get() = profile?.mediaEntity


val UserResponse.model
    get() = User(
        id = id,
        username = username,
        profile = profile!!.model,
        session = session?.model
    )
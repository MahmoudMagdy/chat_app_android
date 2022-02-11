package com.android.chatapp.feature_authentication.domain.model

data class User(
    val id: Long,
    val username: String,
    val profile: Profile,
    val session: Session?
)

val USER_PREVIEW = User(
    id = 1,
    username = "mahmoud_magdy",
    profile = PROFILE_PREVIEW,
    session = SESSION_PREVIEW
)

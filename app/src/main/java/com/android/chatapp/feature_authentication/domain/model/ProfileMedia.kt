package com.android.chatapp.feature_authentication.domain.model

data class ProfileMedia(val id: Long, val media: String)

val PROFILE_MEDIA_PREVIEW = ProfileMedia(
    id = 1,
    media = "https://pbs.twimg.com/profile_images/1275199321718304768/9V8Kev-5_400x400.jpg"
)


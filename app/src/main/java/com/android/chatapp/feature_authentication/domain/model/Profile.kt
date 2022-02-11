package com.android.chatapp.feature_authentication.domain.model

data class Profile(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val quote: String?,
    val description: String?,
    val gender: Gender,
    val image: ProfileMedia?
)

val Profile.name get() = "$firstName $lastName"

val PROFILE_PREVIEW = Profile(
    id = 1,
    firstName = "Mahmoud",
    lastName = "Magdy",
    quote = "Think positive no matter how hard life is 💖.",
    description = "“Enjoy moments before they become memories“",
    gender = Gender.MALE,
    image = PROFILE_MEDIA_PREVIEW
)
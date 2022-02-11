package com.android.chatapp.feature_authentication.data.remote.dto

import com.android.chatapp.feature_authentication.domain.model.Gender
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateProfileRequest(
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("country_code")
    val countryCode: String,
    @SerialName("device_language")
    val deviceLanguage: String,
    val gender: Gender,
    val birthdate: LocalDate,
    @SerialName("latest_image")
    val image: ProfileMediaRequest?
)

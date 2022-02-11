package com.android.chatapp.feature_authentication.data.remote.dto

import com.android.chatapp.feature_authentication.data.local.entity.ProfileEntity
import com.android.chatapp.feature_authentication.domain.model.Gender
import com.android.chatapp.feature_authentication.domain.model.Profile
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val id: Long,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("country_code")
    val countryCode: String,
    @SerialName("device_language")
    val deviceLanguage: String,
    val quote: String?,
    val description: String?,
    val gender: Gender,
    val birthdate: LocalDate,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("updated_at")
    val updatedAt: Instant,
    @SerialName("latest_image")
    val latestImage: ProfileMediaResponse? = null,
    @SerialName("user_id")
    val userId: Long
)

val ProfileResponse.entity
    get() =
        ProfileEntity(
            id, firstName, lastName, gender, birthdate, countryCode,
            deviceLanguage, quote, description, createdAt, updatedAt, userId
        )

val ProfileResponse.mediaEntity get() = latestImage?.entity

val ProfileResponse.model
    get() = Profile(id, firstName, lastName, quote, description, gender, latestImage?.model)

package com.android.chatapp.feature_authentication.data.local.entity


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.chatapp.feature_authentication.domain.model.Gender
import com.android.chatapp.feature_authentication.domain.model.Profile
import com.android.chatapp.feature_authentication.domain.model.ProfileMedia
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

@Entity(tableName = "profiles")
data class ProfileEntity(
    @ColumnInfo(name = "profile_id")
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    @ColumnInfo(name = "first_name")
    val firstName: String,
    @ColumnInfo(name = "last_name")
    val lastName: String,
    val gender: Gender,
    val birthdate: LocalDate,
    @ColumnInfo(name = "country_code")
    val countryCode: String,
    @ColumnInfo(name = "device_language")
    val deviceLanguage: String,
    val quote: String?,
    val description: String?,
    @ColumnInfo(name = "profile_created_at")
    val createdAt: Instant,
    @ColumnInfo(name = "profile_updated_at")
    val updatedAt: Instant,
    @ColumnInfo(name = "profile_user_id")
    val userId: Long
)

fun ProfileEntity.model(media: ProfileMedia?) =
    Profile(id, firstName, lastName, quote, description, gender, media)
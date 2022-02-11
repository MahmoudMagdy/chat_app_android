package com.android.chatapp.feature_authentication.data.local.entity


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.chatapp.feature_authentication.domain.model.ProfileMedia
import com.android.chatapp.feature_authentication.domain.model.ProfileMediaType
import kotlinx.datetime.Instant

@Entity(tableName = "profile_media")
data class ProfileMediaEntity(
    @ColumnInfo(name = "profile_media_id")
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val media: String,
    @ColumnInfo(name = "profile_media_name")
    val name: String,
    @ColumnInfo(name = "profile_media_type")
    val type: ProfileMediaType,
    val extension: String,
    @ColumnInfo(name = "profile_media_size")
    val size: Long,
    @ColumnInfo(name = "profile_media_created_at")
    val createdAt: Instant,
    @ColumnInfo(name = "profile_media_profile_id")
    val profileId: Long
)

val ProfileMediaEntity.model get() = ProfileMedia(id, media)
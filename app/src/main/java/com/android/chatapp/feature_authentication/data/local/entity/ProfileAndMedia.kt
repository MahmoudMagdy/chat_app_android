package com.android.chatapp.feature_authentication.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.android.chatapp.feature_authentication.domain.model.Profile

data class ProfileAndMedia(
    @Embedded val profile: ProfileEntity,
    @Relation(
        parentColumn = "profile_id",
        entityColumn = "profile_media_profile_id"
    )
    val media: List<ProfileMediaEntity>
)

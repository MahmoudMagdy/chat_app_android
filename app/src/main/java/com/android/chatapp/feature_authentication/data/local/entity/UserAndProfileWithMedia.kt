package com.android.chatapp.feature_authentication.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class UserAndProfileWithMedia(
    @Embedded val user: UserEntity,
    @Relation(
        entity = ProfileEntity::class,
        parentColumn = "user_id",
        entityColumn = "profile_user_id"
    )
    val profile: ProfileAndMedia?
)

val UserAndProfileWithMedia.model
    get() = profile?.run {
        user.model(
            profile.model(
                if (media.isEmpty()) null
                else media[0].model
            ),
            null
        )
    }
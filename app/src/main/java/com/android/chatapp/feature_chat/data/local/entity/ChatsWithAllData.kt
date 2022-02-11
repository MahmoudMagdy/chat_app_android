package com.android.chatapp.feature_chat.data.local.entity

import androidx.room.Embedded
import com.android.chatapp.feature_authentication.data.local.entity.ProfileEntity
import com.android.chatapp.feature_authentication.data.local.entity.ProfileMediaEntity
import com.android.chatapp.feature_authentication.data.local.entity.UserEntity
import com.android.chatapp.feature_authentication.data.local.entity.model
import com.android.chatapp.feature_authentication.data.remote.dto.UserResponse
import com.android.chatapp.feature_authentication.data.remote.dto.model

data class ChatsWithAllData(
    @Embedded
    val chat: ChatEntity,
    @Embedded
    val user: UserEntity,
    @Embedded
    val profile: ProfileEntity,
    @Embedded
    val profileMedia: ProfileMediaEntity?,
    @Embedded
    val latestMessage: MessageEntity
)

fun ChatsWithAllData.model(users: List<UserResponse>?) =
    chat.model(
        user = user.model(
            profile = profile.model(profileMedia?.model),
            session = users?.find { it.id == user.id }?.session?.model
        ),
        message = latestMessage.model
    )

fun List<ChatsWithAllData>.models(users: List<UserResponse>?) = map { it.model(users) }
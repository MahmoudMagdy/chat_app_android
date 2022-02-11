package com.android.chatapp.feature_chat.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.android.chatapp.feature_authentication.data.local.entity.UserAndProfileWithMedia
import com.android.chatapp.feature_authentication.data.local.entity.UserEntity

data class ChatWithUsers(
    @Embedded
    val chat: ChatEntity,
    @Relation(
        entity = UserEntity::class,
        parentColumn = "chat_id",
        entityColumn = "user_id",
        associateBy = Junction(UserChatCrossRef::class)
    )
    val usersAndProfile: List<UserAndProfileWithMedia>
)

/**
 * @param cid stands for current user id.
 * @param message Latest message in the chat.
 *//*

fun ChatWithUsers.model(cid: Long, message: Message) = Chat(
    id = chat.id,
    type = chat.type,
    title = chat.title,
    profile = usersAndProfile.filter { it.user.id != cid }.map { it.profile.model },
    latestMessage = message
)*/

package com.android.chatapp.feature_chat.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.android.chatapp.feature_authentication.data.local.entity.UserEntity
import com.android.chatapp.feature_chat.domain.model.Message

data class UserWithChats(
    @Embedded val user: UserEntity,
    @Relation(
        entity = ChatEntity::class,
        parentColumn = "user_id",
        entityColumn = "chat_id",
        associateBy = Junction(UserChatCrossRef::class)
    )
    val chats: List<ChatWithUsers>
)



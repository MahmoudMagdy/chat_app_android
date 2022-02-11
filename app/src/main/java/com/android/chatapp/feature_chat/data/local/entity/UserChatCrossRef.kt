package com.android.chatapp.feature_chat.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "user_chat_cross_ref", primaryKeys = ["user_id", "chat_id"])
data class UserChatCrossRef(
    @ColumnInfo(name = "user_id")
    val userId: Long,
    @ColumnInfo(name = "chat_id")
    val chatId: Long,
)

package com.android.chatapp.feature_chat.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.chatapp.feature_authentication.domain.model.Profile
import com.android.chatapp.feature_authentication.domain.model.User
import com.android.chatapp.feature_chat.domain.model.Chat
import com.android.chatapp.feature_chat.domain.model.ChatType
import com.android.chatapp.feature_chat.domain.model.Message
import kotlinx.datetime.Instant

@Entity(tableName = "chats")
data class ChatEntity(
    @ColumnInfo(name = "chat_id")
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    @ColumnInfo(name = "chat_type")
    val type: ChatType,
    @ColumnInfo(name = "chat_title")
    val title: String?,
    @ColumnInfo(name = "chat_created_at")
    val createdAt: Instant,
    @ColumnInfo(name = "chat_updated_at")
    val updatedAt: Instant,
)

fun ChatEntity.model(user: User, message: Message) =
    Chat(id, type, title, user, message)
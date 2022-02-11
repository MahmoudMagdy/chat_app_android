package com.android.chatapp.feature_chat.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.chatapp.feature_chat.domain.model.Message
import com.android.chatapp.feature_chat.domain.model.MessageType
import kotlinx.datetime.Instant

@Entity(tableName = "messages")
data class MessageEntity(
    @ColumnInfo(name = "message_id")
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    @ColumnInfo(name = "message_user_id")
    val userId: Long,
    @ColumnInfo(name = "message_chat_id")
    val chatId: Long,
    @ColumnInfo(name = "message_type")
    val type: MessageType,
    @ColumnInfo(name = "message_content")
    val content: String,
    @ColumnInfo(name = "message_created_at")
    val createdAt: Instant
)

val MessageEntity.model get() = Message(id, userId, content, type, createdAt)
val List<MessageEntity>.models get() = map { it.model }

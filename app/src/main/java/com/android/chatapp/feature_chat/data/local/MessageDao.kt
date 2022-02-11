package com.android.chatapp.feature_chat.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.chatapp.core.data.util.BASE_PAGE_SIZE
import com.android.chatapp.feature_chat.data.local.entity.MessageEntity

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(messages: List<MessageEntity>)

    @Query(
        "SELECT * FROM `messages` WHERE `message_chat_id` = :chatId AND `message_id` < :latestId " +
                "ORDER BY `message_created_at` DESC LIMIT :limit"
    )
    suspend fun getChatMessages(
        chatId: Long,
        latestId: Long,
        limit: Int = BASE_PAGE_SIZE
    ): List<MessageEntity>

    @Query(
        "SELECT * FROM `messages` WHERE `message_chat_id` = :chatId " +
                "ORDER BY `message_created_at` DESC LIMIT :limit"
    )
    suspend fun getStartChatMessages(
        chatId: Long,
        limit: Int = BASE_PAGE_SIZE
    ): List<MessageEntity>
}
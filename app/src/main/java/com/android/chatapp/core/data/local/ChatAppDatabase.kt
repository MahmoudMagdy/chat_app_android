package com.android.chatapp.core.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.android.chatapp.feature_authentication.data.local.ProfileDao
import com.android.chatapp.feature_authentication.data.local.UserDao
import com.android.chatapp.feature_authentication.data.local.entity.ProfileEntity
import com.android.chatapp.feature_authentication.data.local.entity.ProfileMediaEntity
import com.android.chatapp.feature_authentication.data.local.entity.UserEntity
import com.android.chatapp.feature_chat.data.local.ChatDao
import com.android.chatapp.feature_chat.data.local.MessageDao
import com.android.chatapp.feature_chat.data.local.entity.ChatEntity
import com.android.chatapp.feature_chat.data.local.entity.MessageEntity
import com.android.chatapp.feature_chat.data.local.entity.UserChatCrossRef

@Database(
    entities =
    [
        UserEntity::class,
        ProfileEntity::class,
        ProfileMediaEntity::class,
        ChatEntity::class,
        MessageEntity::class,
        UserChatCrossRef::class,
    ],
    version = 1
)
@TypeConverters(RoomDateConverter::class)
abstract class ChatAppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun profileDao(): ProfileDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var instance: ChatAppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ChatAppDatabase::class.java,
                "chat_app.db"
            ).build()
    }
}
package com.android.chatapp.feature_chat.data.local

import androidx.room.*
import com.android.chatapp.feature_authentication.data.local.entity.ProfileEntity
import com.android.chatapp.feature_authentication.data.local.entity.ProfileMediaEntity
import com.android.chatapp.feature_authentication.data.local.entity.UserEntity
import com.android.chatapp.feature_chat.data.local.entity.ChatEntity
import com.android.chatapp.feature_chat.data.local.entity.ChatsWithAllData
import com.android.chatapp.feature_chat.data.local.entity.MessageEntity
import com.android.chatapp.feature_chat.data.local.entity.UserChatCrossRef

@Dao
interface ChatDao {
    /*
        @Transaction
        @Query("SELECT * FROM `chats` WHERE chat_id = :id")
        suspend fun getChatWithUsers(id: Long): ChatWithUsers
    */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chats: ChatEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(messages: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(chats: List<ChatEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserChatCrossRefs(crossRefs: List<UserChatCrossRef>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfiles(profiles: List<ProfileEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfileMediaList(profiles: List<ProfileMediaEntity>)


    @Transaction
    @Query(
        "SELECT `chats`.*,u2.*,p.*,pm.*, m1.* FROM `users` AS u1 " +
                "JOIN `user_chat_cross_ref` AS ucc1 ON u1.user_id = ucc1.user_id " +
                "JOIN `chats` ON ucc1.chat_id = `chats`.chat_id " +
                "JOIN `user_chat_cross_ref` AS ucc2 ON `chats`.chat_id = ucc2.chat_id " +
                "JOIN `users` AS u2 ON (ucc2.user_id = u2.user_id AND u2.user_id <> :uid) " +
                "JOIN `profiles` AS p ON u2.user_id = p.profile_user_id " +
                "LEFT JOIN `profile_media` AS pm ON p.profile_id = pm.profile_media_profile_id " +
                "LEFT JOIN `profile_media` AS pm2 ON (p.profile_id = pm2.profile_media_profile_id " +
                "AND pm.profile_media_created_at < pm2.profile_media_created_at)" +
                "JOIN `messages` AS m1 ON `chats`.chat_id = m1.message_chat_id " +
                "LEFT JOIN `messages` AS m2 ON (`chats`.chat_id = m2.message_chat_id AND m1.message_created_at < m2.message_created_at) " +
                "WHERE u1.user_id = :uid AND m2.message_id IS NULL AND pm2.profile_media_id IS NULL " +
                "ORDER BY m1.message_created_at DESC"
    )
    suspend fun getChatsWithAllData(uid: Long): List<ChatsWithAllData>

    @Transaction
    @Query(
        "SELECT `chats`.*,u.*,p.*,pm.*, m1.* FROM `chats` " +
                "JOIN `user_chat_cross_ref` AS ucc ON `chats`.chat_id = ucc.chat_id " +
                "JOIN `users` AS u ON (ucc.user_id = u.user_id AND u.user_id <> :uid) " +
                "JOIN `profiles` AS p ON u.user_id = p.profile_user_id " +
                "LEFT JOIN `profile_media` AS pm ON p.profile_id = pm.profile_media_profile_id " +
                "LEFT JOIN `profile_media` AS pm2 ON (p.profile_id = pm2.profile_media_profile_id " +
                "AND pm.profile_media_created_at < pm2.profile_media_created_at)" +
                "JOIN `messages` AS m1 ON `chats`.chat_id = m1.message_chat_id " +
                "LEFT JOIN `messages` AS m2 ON (`chats`.chat_id = m2.message_chat_id AND m1.message_created_at < m2.message_created_at) " +
                "WHERE `chats`.chat_id = :cid AND m2.message_id IS NULL AND pm2.profile_media_id IS NULL " +
                "ORDER BY m1.message_created_at DESC"
    )
    suspend fun getChatWithAllData(uid: Long, cid: Long): ChatsWithAllData

/*
    @Transaction
    @Query("SELECT * FROM `users` WHERE user_id = :id")
    suspend fun getUserWithChats(id: Long): UserWithChats
*/


    @Transaction
    suspend fun insertChatsWithAllData(
        chatEntities: List<ChatEntity>,
        messageEntities: List<MessageEntity>,
        userEntities: List<UserEntity>,
        userChatCrossRefEntities: List<UserChatCrossRef>,
        profileEntities: List<ProfileEntity>,
        mediaEntities: List<ProfileMediaEntity>
    ) {
        insertList(chatEntities)
        insertMessages(messageEntities)
        insertUsers(userEntities)
        insertUserChatCrossRefs(userChatCrossRefEntities)
        insertProfiles(profileEntities)
        if (mediaEntities.isNotEmpty()) insertProfileMediaList(mediaEntities)
    }


    @Transaction
    suspend fun insertChatWithAllData(
        chatEntity: ChatEntity,
        messageEntity: MessageEntity,
        userEntities: List<UserEntity>,
        userChatCrossRefEntities: List<UserChatCrossRef>,
        profileEntities: List<ProfileEntity>,
        mediaEntities: List<ProfileMediaEntity>
    ) {
        insert(chatEntity)
        insertMessage(messageEntity)
        insertUsers(userEntities)
        insertUserChatCrossRefs(userChatCrossRefEntities)
        insertProfiles(profileEntities)
        if (mediaEntities.isNotEmpty()) insertProfileMediaList(mediaEntities)
    }
}
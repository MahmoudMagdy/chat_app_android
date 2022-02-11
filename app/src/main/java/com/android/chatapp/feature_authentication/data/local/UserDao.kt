package com.android.chatapp.feature_authentication.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.android.chatapp.feature_authentication.data.local.entity.ProfileEntity
import com.android.chatapp.feature_authentication.data.local.entity.ProfileMediaEntity
import com.android.chatapp.feature_authentication.data.local.entity.UserAndProfileWithMedia
import com.android.chatapp.feature_authentication.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProfile(profile: ProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProfileMedia(profileMedia: ProfileMediaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(users: List<UserEntity>)

    @Update
    fun update(user: UserEntity)

    @Transaction
    @Query("SELECT * FROM `users` WHERE is_logged_in = 1 ORDER BY `last_login` DESC")
    suspend fun getLoggedInUserAndProfileSync(): UserAndProfileWithMedia?

    @Query("SELECT * FROM `users` WHERE is_logged_in = 1 ORDER BY `last_login` DESC")
    fun getLoggedInUser(): LiveData<UserEntity>

    @Query("SELECT * FROM `users` WHERE user_id = :id")
    fun getUserAndProfileSync(id: Long): UserAndProfileWithMedia?

    @Transaction
    fun insertUserAndProfile(
        user: UserEntity,
        profile: ProfileEntity?,
        profileMedia: ProfileMediaEntity?
    ) {
        insert(user)
        if (profile != null) insertProfile(profile)
        if (profileMedia != null) insertProfileMedia(profileMedia)
    }

    @Transaction
    suspend fun logout() {
        val user = getLoggedInUserAndProfileSync()?.user
        if (user != null)
            update(user.logout)
    }

}
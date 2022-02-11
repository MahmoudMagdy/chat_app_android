package com.android.chatapp.feature_authentication.data.local

import androidx.room.*
import com.android.chatapp.feature_authentication.data.local.entity.ProfileAndMedia
import com.android.chatapp.feature_authentication.data.local.entity.ProfileMediaEntity
import com.android.chatapp.feature_authentication.data.local.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(profile: ProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMedia(media: ProfileMediaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(profiles: List<ProfileEntity>)

    @Transaction
    fun insertProfileAndMedia(profile: ProfileEntity, media: ProfileMediaEntity?) {
        insert(profile)
        if (media != null) insertMedia(media)
    }

    @Update
    fun update(profile: ProfileEntity)

    @Query("SELECT * FROM `profiles` WHERE profile_user_id = :uid")
    fun getUserProfile(uid: Long): Flow<ProfileEntity>

    @Transaction
    @Query("SELECT * FROM `profiles` WHERE profile_user_id = :uid")
    fun getUserAndProfile(uid: Long): Flow<ProfileAndMedia?>

}
package com.android.chatapp.feature_authentication.data.local.entity


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.chatapp.feature_authentication.domain.model.Profile
import com.android.chatapp.feature_authentication.domain.model.Session
import com.android.chatapp.feature_authentication.domain.model.User
import kotlinx.datetime.Instant

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "user_id")
    val id: Long,
    val email: String,
    val username: String,
    @ColumnInfo(name = "is_verified")
    val isVerified: Boolean,
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    @ColumnInfo(name = "is_staff")
    val isStaff: Boolean,
    @ColumnInfo(name = "user_created_at")
    val createdAt: Instant,
    @ColumnInfo(name = "user_updated_at")
    val updatedAt: Instant,
    @ColumnInfo(name = "last_login")
    val lastLogin: Instant,
    @ColumnInfo(name = "is_logged_in")
    val isLoggedIn: Boolean = false
) {
    val logout
        get() = UserEntity(
            id, email, username, isVerified, isActive,
            isStaff, createdAt, updatedAt, lastLogin
        )
}

fun UserEntity.model(profile: Profile, session: Session?) =
    User(id, username, profile, session)
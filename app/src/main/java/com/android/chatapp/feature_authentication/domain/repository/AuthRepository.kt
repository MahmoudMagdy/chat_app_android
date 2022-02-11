package com.android.chatapp.feature_authentication.domain.repository

import com.android.chatapp.core.data.util.ApiException
import com.android.chatapp.core.data.util.PaginationResult
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_authentication.data.local.entity.ProfileAndMedia
import com.android.chatapp.feature_authentication.data.remote.dto.*
import com.android.chatapp.feature_authentication.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    fun isEmailDateExceededTimeLimit(email: String, mills: Long): Boolean
    fun logout()
    fun login(loginRequest: LoginRequest): Flow<Resource<UserResponse, ApiException>>
    fun register(registerRequest: RegisterRequest): Flow<Resource<UserResponse, ApiException>>
    fun resetPassword(resetRequest: ResetPasswordRequest): Flow<Resource<ResetPasswordResponse, ApiException>>
    fun generateProfileUploadUrl(profileUploadUrlRequest: ProfileUploadUrlRequest): Flow<Resource<ProfileUploadUrlResponse, ApiException>>
    fun createProfile(profileRequest: CreateProfileRequest): Flow<Resource<ProfileResponse, ApiException>>
    fun searchUsers(
        keyword: String,
        page: Int
    ): Flow<Resource<PaginationResult<User>, ApiException>>

    fun getUser(id: Long): Flow<Resource<User, ApiException>>

    suspend fun getCurrentUserProfile(): StateFlow<Resource<ProfileAndMedia, ApiException>?>
    fun loggedIn(): Flow<Boolean>
}
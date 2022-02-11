package com.android.chatapp.feature_authentication.data.remote

import com.android.chatapp.core.data.util.BASE_PAGE_SIZE
import com.android.chatapp.core.data.util.PaginationResult
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_authentication.data.remote.dto.*
import kotlinx.coroutines.flow.SharedFlow

interface AuthService {
    val logout: SharedFlow<Boolean>
    suspend fun register(registerRequest: RegisterRequest): Resource.Success<UserResponse>
    suspend fun login(loginRequest: LoginRequest): Resource.Success<UserResponse>
    suspend fun logout()
    suspend fun refreshTokens(refreshToken: RefreshTokensRequest): Resource.Success<RefreshTokensResponse>
    suspend fun resetPassword(resetRequest: ResetPasswordRequest): Resource.Success<ResetPasswordResponse>
    suspend fun generateProfileUploadUrl(uploadUrlRequest: ProfileUploadUrlRequest): Resource.Success<ProfileUploadUrlResponse>
    suspend fun createProfile(profileRequest: CreateProfileRequest): Resource.Success<ProfileResponse>
    suspend fun getCurrentUserProfile(): Resource.Success<ProfileResponse>
    suspend fun getUser(id: Long): Resource.Success<UserResponse>
    suspend fun searchUsers(
        keyword: String,
        page: Int,
        limit: Int = BASE_PAGE_SIZE
    ): Resource.Success<PaginationResult<UserResponse>>
}
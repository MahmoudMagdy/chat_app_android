package com.android.chatapp.feature_authentication.data.remote

import com.android.chatapp.core.data.util.PaginationResult
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_authentication.data.remote.dto.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AuthServiceImpl(private val client: HttpClient, private val authenticatedClient: HttpClient) :
    AuthService {
    private val _logout = MutableSharedFlow<Boolean>()
    override val logout: SharedFlow<Boolean>
        get() = _logout.asSharedFlow()

    override suspend fun register(registerRequest: RegisterRequest): Resource.Success<UserResponse> =
        client.post {
            url(HttpRoutes.REGISTER_URL)
            contentType(ContentType.Application.Json)
            body = registerRequest
        }


    override suspend fun login(loginRequest: LoginRequest): Resource.Success<UserResponse> =
        client.post {
            url(HttpRoutes.LOGIN_URL)
            contentType(ContentType.Application.Json)
            body = loginRequest
        }

    override suspend fun logout() {
        _logout.emit(true)
    }


    override suspend fun resetPassword(resetRequest: ResetPasswordRequest): Resource.Success<ResetPasswordResponse> =
        client.post {
            url(HttpRoutes.RESET_PASSWORD_URL)
            contentType(ContentType.Application.Json)
            body = resetRequest
        }

    override suspend fun refreshTokens(refreshToken: RefreshTokensRequest): Resource.Success<RefreshTokensResponse> =
        client.post {
            url(HttpRoutes.REFRESH_TOKENS_URL)
            contentType(ContentType.Application.Json)
            body = refreshToken
        }


    override suspend fun generateProfileUploadUrl(uploadUrlRequest: ProfileUploadUrlRequest): Resource.Success<ProfileUploadUrlResponse> =
        authenticatedClient.post {
            url(HttpRoutes.PROFILE_UPLOAD_URL)
            contentType(ContentType.Application.Json)
            body = uploadUrlRequest
        }

    override suspend fun createProfile(profileRequest: CreateProfileRequest): Resource.Success<ProfileResponse> =
        authenticatedClient.post {
            url(HttpRoutes.CREATE_PROFILE_URL)
            contentType(ContentType.Application.Json)
            body = profileRequest
        }

    override suspend fun getCurrentUserProfile(): Resource.Success<ProfileResponse> =
        authenticatedClient.get { url(HttpRoutes.CURRENT_PROFILE_URL) }

    override suspend fun getUser(id: Long): Resource.Success<UserResponse> =
        authenticatedClient.get { url(HttpRoutes.getUserUrl(id)) }

    override suspend fun searchUsers(
        keyword: String,
        page: Int,
        limit: Int
    ): Resource.Success<PaginationResult<UserResponse>> =
        authenticatedClient.get { url(HttpRoutes.getSearchUrl(keyword, page, limit)) }
}
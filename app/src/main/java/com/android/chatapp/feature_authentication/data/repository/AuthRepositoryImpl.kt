package com.android.chatapp.feature_authentication.data.repository

import com.android.chatapp.core.data.util.ApiException
import com.android.chatapp.core.data.util.PaginationResult
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.core.data.util.safeApiCall
import com.android.chatapp.feature_authentication.data.local.ProfileDao
import com.android.chatapp.feature_authentication.data.local.UserDao
import com.android.chatapp.feature_authentication.data.local.entity.ProfileAndMedia
import com.android.chatapp.feature_authentication.data.local.entity.model
import com.android.chatapp.feature_authentication.data.provider.reset_password.ResetPasswordProvider
import com.android.chatapp.feature_authentication.data.provider.token.TokenProvider
import com.android.chatapp.feature_authentication.data.remote.AuthService
import com.android.chatapp.feature_authentication.data.remote.dto.*
import com.android.chatapp.feature_authentication.data.util.AuthApiExceptions
import com.android.chatapp.feature_authentication.data.util.UnauthorizedAccessException
import com.android.chatapp.feature_authentication.domain.model.User
import com.android.chatapp.feature_authentication.domain.repository.AuthRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class AuthRepositoryImpl constructor(
    private val service: AuthService,
    private val dao: UserDao,
    private val profileDao: ProfileDao,
    private val provider: TokenProvider,
    private val resetProvider: ResetPasswordProvider
) : AuthRepository {

    private val _currentProfile: MutableStateFlow<Resource<ProfileAndMedia, ApiException>?> =
        MutableStateFlow(null)
    private val currentProfile: StateFlow<Resource<ProfileAndMedia, ApiException>?>
        get() = _currentProfile.asStateFlow()
    private var listeningScope: Job? = null

    init {
        GlobalScope.launch(Dispatchers.IO) {
            val uid = provider.uid()
            if (uid != null) listenToCurrentProfile(uid)
        }
    }

    override fun login(loginRequest: LoginRequest): Flow<Resource<UserResponse, ApiException>> =
        flow {
            emit(Resource.Loading())
            val loginResponse = safeApiCall<UserResponse, ApiException> {
                service.login(loginRequest)
            }
            if (loginResponse is Resource.Success) {
                saveAccessTokens(loginResponse.data)
                persistFetchedUserAndProfile(loginResponse.data)
            }
            emit(loginResponse)
        }

    override fun register(registerRequest: RegisterRequest): Flow<Resource<UserResponse, ApiException>> =
        flow {
            emit(Resource.Loading())
            val loginResponse = safeApiCall<UserResponse, ApiException> {
                service.register(registerRequest)
            }
            if (loginResponse is Resource.Success) {
                saveAccessTokens(loginResponse.data)
                persistFetchedUserAndProfile(loginResponse.data)
            }
            emit(loginResponse)
        }

    override fun resetPassword(resetRequest: ResetPasswordRequest): Flow<Resource<ResetPasswordResponse, ApiException>> =
        flow {
            emit(Resource.Loading())
            val resetResponse = safeApiCall<ResetPasswordResponse, ApiException> {
                service.resetPassword(resetRequest)
            }
            if (resetResponse is Resource.Success) saveResetEmail(resetRequest.email)
            emit(resetResponse)
        }

    override fun generateProfileUploadUrl(profileUploadUrlRequest: ProfileUploadUrlRequest): Flow<Resource<ProfileUploadUrlResponse, ApiException>> =
        flow {
            emit(Resource.Loading())
            emit(safeApiCall {
                service.generateProfileUploadUrl(profileUploadUrlRequest)
            })
        }


    override fun createProfile(profileRequest: CreateProfileRequest): Flow<Resource<ProfileResponse, ApiException>> =
        flow {
            emit(Resource.Loading())
            val profileResponse = safeApiCall<ProfileResponse, ApiException> {
                service.createProfile(profileRequest)
            }
            if (profileResponse is Resource.Success) {
                persistFetchedProfileMedia(profileResponse.data)
            }
            emit(profileResponse)
        }

    override fun searchUsers(
        keyword: String,
        page: Int
    ): Flow<Resource<PaginationResult<User>, ApiException>> = flow {
        emit(Resource.Loading())
        val response = safeApiCall<PaginationResult<UserResponse>, ApiException> {
            service.searchUsers(keyword, page)
        }
        val result = when (response) {
            is Resource.Error -> Resource.Error(response.throwable)
            is Resource.Success -> Resource.Success(
                response.data.run {
                    PaginationResult(
                        next = next,
                        previous = previous,
                        results = results.filter { it.profile != null }.map { it.model }
                    )
                }
            )
            is Resource.Failure -> Resource.Failure(response.errors)
            is Resource.Loading -> Resource.Loading()
        }
        emit(result)
    }

    override fun getUser(id: Long): Flow<Resource<User, ApiException>> = flow {
        emit(Resource.Loading())
        coroutineScope {
            val daoResponse = async { dao.getUserAndProfileSync(id) }
            val remoteResponse = async {
                safeApiCall<UserResponse, ApiException> { service.getUser(id) }
            }
            val daoResult = daoResponse.await()?.model
            if (daoResult != null) emit(Resource.Loading(daoResult))
            when (val result = remoteResponse.await()) {
                is Resource.Success -> {
                    result.data.apply {
                        async {
                            dao.insertUserAndProfile(
                                loggedEntity,
                                profile?.entity,
                                profile?.latestImage?.entity
                            )
                        }
                        emit(Resource.Success(model))
                    }
                }
                is Resource.Error -> emit(Resource.Error(result.throwable, daoResult))
                is Resource.Failure -> emit(Resource.Failure(result.errors, daoResult))
                is Resource.Loading -> Unit
            }
        }
    }


    @Throws(UnauthorizedAccessException::class)
    override suspend fun getCurrentUserProfile(): StateFlow<Resource<ProfileAndMedia, ApiException>?> {
        val uid = provider.uid() ?: throw UnauthorizedAccessException.NoUserID
        if (listeningScope == null) listenToCurrentProfile(uid)
        //TODO: Make a time threshold after that fetch for profile if it was not exceeded not make a fetch
        GlobalScope.launch(Dispatchers.IO) {
            val profileResponse = safeApiCall<ProfileResponse, ApiException> {
                service.getCurrentUserProfile()
            }
            when (profileResponse) {
                is Resource.Success -> persistFetchedProfileMedia(profileResponse.data)
                is Resource.Failure -> if (profileResponse.errors.run {
                        forEach {
                            if (
                                it == AuthApiExceptions.notAuthenticated ||
                                it == AuthApiExceptions.notAuthenticatedToken ||
                                it == AuthApiExceptions.profileNotFound
                            )
                                return@run true
                        }
                        return@run false
                    })
                    _currentProfile.value = profileResponse.run { Resource.Failure(errors) }
                is Resource.Error/*,
                is Resource.NetworkError*/ -> {
                }
            }
        }
        return currentProfile
    }

    override fun loggedIn(): Flow<Boolean> = flow {
        val uid = provider.uid()
        if (uid == null) {
            emit(false)
            return@flow
        }
        val userAndProfile = dao.getLoggedInUserAndProfileSync()
        emit(userAndProfile?.profile != null && userAndProfile.user.id == uid)
    }

    private fun listenToCurrentProfile(uid: Long) {
        listeningScope = GlobalScope.launch(Dispatchers.IO) {
            profileDao.getUserAndProfile(uid).filterNotNull().collect {
                _currentProfile.value = Resource.Success(it)
            }
        }
    }

    private fun persistFetchedUserAndProfile(loginResponse: UserResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            loginResponse.apply {
                dao.insertUserAndProfile(
                    loggedEntity,
                    profile?.entity,
                    profile?.latestImage?.entity
                )
            }
        }
    }

    private fun persistFetchedProfileMedia(profileResponse: ProfileResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            profileResponse.apply {
                profileDao.insertProfileAndMedia(entity, latestImage?.entity)
            }
        }
    }

    private fun saveResetEmail(email: String) {
        resetProvider.setEmailDate(email)
    }

    private suspend fun saveAccessTokens(loginResponse: UserResponse) {
        provider.set(loginResponse.preferences)
    }

    override fun isEmailDateExceededTimeLimit(email: String, mills: Long): Boolean =
        resetProvider.isEmailDateExceededTimeLimit(email, mills)

    override fun logout() {
        GlobalScope.launch(Dispatchers.IO) {
            listeningScope?.cancel()
            listeningScope = null
            _currentProfile.value = null
            dao.logout()
            provider.logout()
            service.logout()
        }
    }
}


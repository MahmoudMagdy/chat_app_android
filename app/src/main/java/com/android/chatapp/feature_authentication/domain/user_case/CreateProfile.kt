package com.android.chatapp.feature_authentication.domain.user_case

import com.android.chatapp.core.data.util.ApiException
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_authentication.data.remote.dto.CreateProfileRequest
import com.android.chatapp.feature_authentication.data.remote.dto.ProfileResponse
import com.android.chatapp.feature_authentication.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateProfile @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(createProfileRequest: CreateProfileRequest): Flow<Resource<ProfileResponse, ApiException>> =
        repository.createProfile(createProfileRequest)
}


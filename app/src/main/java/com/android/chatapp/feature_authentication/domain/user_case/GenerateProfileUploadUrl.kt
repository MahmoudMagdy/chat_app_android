package com.android.chatapp.feature_authentication.domain.user_case

import com.android.chatapp.core.data.util.ApiException
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_authentication.data.remote.dto.ProfileUploadUrlRequest
import com.android.chatapp.feature_authentication.data.remote.dto.ProfileUploadUrlResponse
import com.android.chatapp.feature_authentication.domain.model.ProfileMediaType
import com.android.chatapp.feature_authentication.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GenerateProfileUploadUrl @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(
        type: ProfileMediaType,
        extension: String,
        size: Long
    ): Flow<Resource<ProfileUploadUrlResponse, ApiException>> =
        repository.generateProfileUploadUrl(ProfileUploadUrlRequest(type, extension, size))
}


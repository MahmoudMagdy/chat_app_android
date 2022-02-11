package com.android.chatapp.feature_authentication.domain.user_case

import com.android.chatapp.core.data.util.ApiException
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_authentication.data.remote.dto.ResetPasswordRequest
import com.android.chatapp.feature_authentication.data.remote.dto.ResetPasswordResponse
import com.android.chatapp.feature_authentication.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ResetPassword @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(resetRequest: ResetPasswordRequest): Flow<Resource<ResetPasswordResponse, ApiException>> =
        repository.resetPassword(resetRequest)
}


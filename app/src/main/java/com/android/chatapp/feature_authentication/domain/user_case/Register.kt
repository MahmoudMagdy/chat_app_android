package com.android.chatapp.feature_authentication.domain.user_case

import com.android.chatapp.core.data.util.ApiException
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_authentication.data.remote.dto.RegisterRequest
import com.android.chatapp.feature_authentication.data.remote.dto.UserResponse
import com.android.chatapp.feature_authentication.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Register @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(registerRequest: RegisterRequest): Flow<Resource<UserResponse, ApiException>> =
        repository.register(registerRequest)

}


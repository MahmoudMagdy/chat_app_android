package com.android.chatapp.feature_chat.domain.use_case

import com.android.chatapp.core.data.util.ApiException
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_authentication.domain.model.User
import com.android.chatapp.feature_authentication.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUser @Inject constructor(private val repository: AuthRepository) {

    /**
     * @param id: id of user
     **/
    operator fun invoke(id: Long): Flow<Resource<User, ApiException>> =
        repository.getUser(id)

}
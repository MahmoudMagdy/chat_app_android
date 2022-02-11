package com.android.chatapp.feature_authentication.domain.user_case

import com.android.chatapp.feature_authentication.domain.repository.AuthRepository
import com.android.chatapp.feature_authentication.presentation.AuthState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class CheckUserLoggedIn @Inject constructor(private val repository: AuthRepository) {

    operator fun invoke(): Flow<AuthState> =
        repository.loggedIn().map { loggedIn ->
            if (loggedIn) AuthState.LOGGED_IN
            else AuthState.LOGIN
        }
}
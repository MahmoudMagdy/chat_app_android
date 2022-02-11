package com.android.chatapp.feature_authentication.presentation

import androidx.lifecycle.ViewModel
import com.android.chatapp.feature_authentication.domain.user_case.CheckUserLoggedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(checkUserLoggedIn: CheckUserLoggedIn) :
    ViewModel() {

    val authState: Flow<AuthState> = checkUserLoggedIn()
    var keepDrawn = true
        private set

    fun removeSplash() {
        keepDrawn = false
    }
}
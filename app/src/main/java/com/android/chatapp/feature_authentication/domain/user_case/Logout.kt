package com.android.chatapp.feature_authentication.domain.user_case

import com.android.chatapp.feature_authentication.domain.repository.AuthRepository
import javax.inject.Inject

class Logout @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke() = repository.logout()
    /*
        if (authManager.isLoggedIn) {
            val uid = authManager.uid
            if (uid != null && !authManager.isAnonymous)
                repository.setUserOffline(uid)
        }
        //TODO: If Uninstall the app not remove anonymous we should delete it
    */

}


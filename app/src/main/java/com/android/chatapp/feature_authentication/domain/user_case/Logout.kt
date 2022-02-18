package com.android.chatapp.feature_authentication.domain.user_case

import com.android.chatapp.feature_authentication.domain.repository.AuthRepository
import com.android.chatapp.feature_notification.domain.use_case.StopNotifications
import javax.inject.Inject

class Logout @Inject constructor(
    private val repository: AuthRepository,
    private val stopNotifications: StopNotifications
) {
    operator fun invoke() {
        stopNotifications()
        repository.logout()
    }
    /*
        //Old Project Code
        if (authManager.isLoggedIn) {
            val uid = authManager.uid
            if (uid != null && !authManager.isAnonymous)
                repository.setUserOffline(uid)
        }
    */

}


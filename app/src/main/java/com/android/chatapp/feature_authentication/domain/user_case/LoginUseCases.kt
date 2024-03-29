package com.android.chatapp.feature_authentication.domain.user_case

import com.android.chatapp.core.domain.user_case.CheckConnection
import com.android.chatapp.feature_notification.domain.use_case.EnqueueNotificationWorker
import javax.inject.Inject

data class LoginUseCases @Inject constructor(
    val login: Login,
    val register: Register,
    val logout: Logout,
    val validateLoginData: ValidateLoginData,
    val validateRegisterData: ValidateRegisterData,
    val checkConnection: CheckConnection,
    val enqueueNotificationWorker: EnqueueNotificationWorker
)
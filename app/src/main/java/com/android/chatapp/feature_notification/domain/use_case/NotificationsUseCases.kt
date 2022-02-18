package com.android.chatapp.feature_notification.domain.use_case

import com.android.chatapp.feature_authentication.domain.user_case.CheckUserLoggedIn
import javax.inject.Inject

class NotificationsUseCases @Inject constructor(
    val checkUserLoggedIn: CheckUserLoggedIn,
    val connect: Connect,
    val createForegroundInfo: CreateForegroundInfo,
    val closeConnection: CloseConnection,
    val stopNotifications: StopNotifications,
    val enqueueNotificationWorker: EnqueueNotificationWorker
)
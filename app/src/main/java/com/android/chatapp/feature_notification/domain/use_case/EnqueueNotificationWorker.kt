package com.android.chatapp.feature_notification.domain.use_case

import androidx.work.*
import com.android.chatapp.feature_authentication.domain.user_case.CheckUserLoggedIn
import com.android.chatapp.feature_authentication.presentation.AuthState
import com.android.chatapp.feature_notification.presentation.util.NOTIFICATION_WORK_NAME
import com.android.chatapp.feature_notification.presentation.worker.NotificationWorker
import com.android.chatapp.feature_notification.presentation.worker.stopNotificationWorker
import kotlinx.coroutines.flow.collect
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class EnqueueNotificationWorker @Inject constructor(
    private val workerManager: WorkManager,
    private val checkUserLoggedIn: CheckUserLoggedIn
) {
    suspend operator fun invoke() {
        workerManager.stopNotificationWorker
        workerManager.pruneWork()
        checkUserLoggedIn().collect {
            if (it == AuthState.LOGGED_IN) {
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
                val notificationWorker =
                    PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .build()
                workerManager.enqueueUniquePeriodicWork(
                    NOTIFICATION_WORK_NAME,
                    ExistingPeriodicWorkPolicy.REPLACE,
                    notificationWorker
                )
            }
        }
    }
}
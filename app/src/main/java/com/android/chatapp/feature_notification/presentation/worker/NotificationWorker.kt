package com.android.chatapp.feature_notification.presentation.worker

import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.android.chatapp.core.data.util.ConnectionNotEstablishedException
import com.android.chatapp.core.data.util.NetworkResponseException
import com.android.chatapp.core.data.util.WSCloseReason
import com.android.chatapp.core.domain.util.logError
import com.android.chatapp.feature_authentication.presentation.AuthState
import com.android.chatapp.feature_notification.domain.model.Notification
import com.android.chatapp.feature_notification.domain.use_case.EnqueueNotificationWorker
import com.android.chatapp.feature_notification.domain.use_case.NotificationsUseCases
import com.android.chatapp.feature_notification.presentation.service.startNotificationService
import com.android.chatapp.feature_notification.presentation.util.NOTIFICATION_WORK_NAME
import com.android.chatapp.feature_notification.presentation.util.createNotification
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext


/**
 * [NotificationWorker]: used to start notification background service if the app in the foreground,
 * or to start the websocket itself if the app in the background or not working, and also if the user
 * logged out and for some reason and service is still running it's worker responsibility to stop the service.
 * **Notes**
 * 1) on the service being destroyed it will re enqueue the worker again to make sure it will work immediately.
 * 2) if the app logged out normally it will stop both service and worker and if it logged in again
 *    it will enqueue the worker again.
 * 3) on the app start it will re enqueue the worker again to close the worker if running and start the service
 *    and it will re enqueue if the user is logged in.
 * 4) if the user logged in it will re enqueue the worker.
 * ***General Notes***
 *> [EnqueueNotificationWorker] will start it as periodic within interval 15 Minutes, and if user is logged in
 * every 10 minutes it will be cancelled as it not ***ForegroundTask*** and within 5 minutes it will
 * start again as soon as work queue not being busy.
 *> This behavior only occurs if [doWork] still suspended (not returned result) until 10 minutes pass
 * which also means worker result is [Result.failure],
 * ***This behavior*** refers to [within 5 minutes and not to wait for all 5 minutes]
 */
@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val cases: NotificationsUseCases
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            cases.checkUserLoggedIn().collect {
                when (it) {
                    AuthState.LOADING -> Unit
                    AuthState.LOGIN -> cases.stopNotifications()
                    AuthState.LOGGED_IN -> {
                        try {
                            applicationContext.startNotificationService
                        } catch (ex: Exception) {
                            //It throws [IllegalStateException] when App in the background,
                            //so I need to start websocket from worker.
                            cases.connect(
                                onReceive = ::onReceive, onClose = ::onClose, onError = ::onError
                            )
                        }
                    }
                }
            }
        } catch (ex: CancellationException) {
            cases.closeConnection()
        }
        Result.success()
    }

    private fun onReceive(notification: Notification) {
        createNotification(applicationContext, notification)
    }

    private fun onClose(reason: WSCloseReason) {
        //TODO: all reasons
        when (reason) {
            WSCloseReason.USER_NOT_FOUND -> logError("Worker:onClose:$reason")
            else -> logError("Worker:onClose:$reason")
        }
    }

    private fun onError(throwable: Throwable) {
        //TODO: all errors
        when (throwable) {
            is NetworkResponseException,
            is ConnectionNotEstablishedException -> logError("Worker:onError:$throwable")
            else -> logError("Worker:onError:$throwable")
        }
    }

    /*
    override suspend fun getForegroundInfo(): ForegroundInfo {
        return cases.createForegroundInfo(id)
    }
    */

}

val WorkManager.stopNotificationWorker
    get() = cancelUniqueWork(NOTIFICATION_WORK_NAME)


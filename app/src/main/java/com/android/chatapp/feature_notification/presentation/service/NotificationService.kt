package com.android.chatapp.feature_notification.presentation.service

import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.android.chatapp.core.data.util.ConnectionNotEstablishedException
import com.android.chatapp.core.data.util.NetworkResponseException
import com.android.chatapp.core.data.util.WSCloseReason
import com.android.chatapp.core.domain.util.logError
import com.android.chatapp.core.presentation.service.CoroutineService
import com.android.chatapp.feature_authentication.presentation.AuthState
import com.android.chatapp.feature_notification.domain.model.Notification
import com.android.chatapp.feature_notification.domain.use_case.NotificationsUseCases
import com.android.chatapp.feature_notification.presentation.util.createNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

val Context.startNotificationService
    get() = startService(Intent(this, NotificationService::class.java))
val Context.stopNotificationService
    get() = stopService(Intent(this, NotificationService::class.java))

@AndroidEntryPoint
class NotificationService : CoroutineService() {

    @Inject
    lateinit var cases: NotificationsUseCases

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onCreate() {
        super.onCreate()
        serviceScope.launch {
            cases.checkUserLoggedIn().collect {
                when (it) {
                    AuthState.LOADING -> Unit
                    AuthState.LOGIN -> cases.stopNotifications()
                    AuthState.LOGGED_IN -> cases.connect(
                        onReceive = ::onReceive,
                        onClose = ::onClose,
                        onError = ::onError
                    )
                }
            }
        }
    }

    private fun onReceive(notification: Notification) {
        createNotification(applicationContext, notification)
    }

    private fun onClose(reason: WSCloseReason) {
        //TODO: all reasons
        when (reason) {
            WSCloseReason.USER_NOT_FOUND -> logError("Service:onClose:$reason")
            else -> logError("Service:onClose:$reason")
        }
    }

    private fun onError(throwable: Throwable) {
        //TODO: all errors
        when (throwable) {
            is NetworkResponseException,
            is ConnectionNotEstablishedException -> logError("Service:onError:$throwable")
            else -> logError("Service:onError:$throwable")
        }
    }

    override suspend fun clear() {
        cases.closeConnection()
        cases.enqueueNotificationWorker()
    }
}
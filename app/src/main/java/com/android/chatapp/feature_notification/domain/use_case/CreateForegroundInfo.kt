package com.android.chatapp.feature_notification.domain.use_case

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import com.android.chatapp.R
import com.android.chatapp.feature_notification.presentation.util.FOREGROUND_CHANNEL_ID
import com.android.chatapp.feature_notification.presentation.util.FOREGROUND_NOTIFICATION_ID
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

/**
 * Creates an instance of ForegroundInfo which can be used to update the
 * ongoing notification.
 **/
class CreateForegroundInfo @Inject constructor(
    @ApplicationContext val context: Context,
    private val workerManager: WorkManager
) {
    operator fun invoke(id: UUID): ForegroundInfo {
        return context.run {
            val intent = workerManager.createCancelPendingIntent(id)
            val notification = NotificationCompat.Builder(context, FOREGROUND_CHANNEL_ID)
                .setContentTitle(getString(R.string.foreground_notification_title))
                .setTicker(getString(R.string.foreground_notification_content))
                .setContentText(getString(R.string.foreground_notification_content))
                .setSmallIcon(R.drawable.ic_chat_app_logo)
                .setOngoing(true)
                // Add the cancel action to the notification which can
                // be used to cancel the worker
                .addAction(
                    android.R.drawable.ic_delete,
                    getString(R.string.foreground_notification_cancel_title),
                    intent
                )
                .build()
            ForegroundInfo(FOREGROUND_NOTIFICATION_ID, notification)
        }
    }
}
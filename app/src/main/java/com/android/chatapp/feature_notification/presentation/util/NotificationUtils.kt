package com.android.chatapp.feature_notification.presentation.util

import android.app.PendingIntent
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.work.WorkManager
import coil.imageLoader
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.android.chatapp.R
import com.android.chatapp.feature_notification.domain.model.Notification
import com.android.chatapp.feature_notification.presentation.service.stopNotificationService
import com.android.chatapp.feature_notification.presentation.worker.stopNotificationWorker

internal const val NOTIFICATION_WORK_NAME = "notification_work_name"

fun stopNotifications(context: Context, workerManager: WorkManager) {
    context.stopNotificationService
    workerManager.stopNotificationWorker
}

internal fun createNotification(
    context: Context,
    notification: Notification,
) {
    //intents are not created if you send the same params. They are reused.
    val requestCode = System.currentTimeMillis().toInt()
    val pendingIntent: PendingIntent =
        PendingIntent.getActivity(
            context, requestCode, notification.intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE
            else 0
        )
    val builder =
        NotificationCompat.Builder(context, DEFAULT_CHANNEL_ID)
            .setSmallIcon(R.drawable.small_logo)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notification.body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            //Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + BuildConfig.APPLICATION_ID + "/" + R.raw.notification_receive)
            //android.resource://[PACKAGE_NAME]/[RESOURCE_ID]
            .setSound(Uri.parse("android.resource://" + context.packageName + "/" + R.raw.notification_receive))
            .setVibrate(longArrayOf(0, 700))
    setNotificationImage(context, notification) { image ->
        builder.setLargeIcon(image?.toBitmap())
        NotificationManagerCompat.from(context).notify(notification.id, builder.build())
    }
//    else notify(otherUser.id, MESSAGE_ID, builder.build())
}

private fun setNotificationImage(
    context: Context,
    notification: Notification,
    onImageLoaded: (Drawable?) -> Unit
) {
    context.imageLoader.enqueue(
        ImageRequest.Builder(context)
            .error(notification.placeholder)
            .data(
                if (!notification.image.isNullOrBlank()) notification.image
                else notification.placeholder
            )
            .transformations(CircleCropTransformation())
            .target(onError = onImageLoaded, onSuccess = onImageLoaded)
            .build()
    )
}



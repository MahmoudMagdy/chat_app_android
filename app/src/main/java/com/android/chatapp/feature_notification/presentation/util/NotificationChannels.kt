package com.android.chatapp.feature_notification.presentation.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import com.android.chatapp.R

internal val Context.createAppChannels
    get() = run {
        createForegroundNotificationChannel()
        createDefaultNotificationChannel()
    }

internal const val DEFAULT_CHANNEL_ID = "default_channel"

/**
 * [DefaultNotificationChannel] : for any notification app receives from the server.
 */
private fun Context.createDefaultNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = getString(R.string.default_channel_name)
        val descriptionText = getString(R.string.default_channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(DEFAULT_CHANNEL_ID, name, importance).apply {
            description = descriptionText
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            setSound(
                Uri.parse("android.resource://" + packageName + "/" + R.raw.notification_receive),
                audioAttributes
            )
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 700)
            shouldVibrate()
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

internal const val FOREGROUND_NOTIFICATION_ID = 101
internal const val FOREGROUND_CHANNEL_ID = "foreground_channel"

/**
 * [ForegroundNotificationChannel] : for [NotificationWorker] to use it to inform user in ***Foreground*** that a background task is working
 */
private fun Context.createForegroundNotificationChannel() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = getString(R.string.foreground_channel_name)
        val descriptionText = getString(R.string.foreground_channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(FOREGROUND_CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

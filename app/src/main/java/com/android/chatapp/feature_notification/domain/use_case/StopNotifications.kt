package com.android.chatapp.feature_notification.domain.use_case

import android.content.Context
import androidx.work.WorkManager
import com.android.chatapp.feature_notification.presentation.util.stopNotifications
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class StopNotifications @Inject constructor(
    @ApplicationContext val context: Context,
    private val workerManager: WorkManager
) {
    operator fun invoke() = stopNotifications(context, workerManager)
}
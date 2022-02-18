package com.android.chatapp.feature_notification.data.remote

import com.android.chatapp.core.data.remote.WSRoutes

object WSRoutes {
    private const val NOTIFICATION_URL = "${WSRoutes.BASE_URL}/notification"
    const val NOTIFICATION_LIST_URL = "$NOTIFICATION_URL/list/"
}
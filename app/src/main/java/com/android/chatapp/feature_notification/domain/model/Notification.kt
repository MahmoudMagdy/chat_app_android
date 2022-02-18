package com.android.chatapp.feature_notification.domain.model

import android.content.Intent
import androidx.annotation.DrawableRes

data class Notification(
    val id: Int,
    val title: String,
    val body: String,
    val image: String?,
    @DrawableRes val placeholder: Int,
    val intent: Intent
)

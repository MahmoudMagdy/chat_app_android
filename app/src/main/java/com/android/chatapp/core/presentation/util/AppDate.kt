package com.android.chatapp.core.presentation.util

import android.text.format.DateUtils
import kotlinx.datetime.Instant


val Instant.readable: String
    get() = DateUtils.getRelativeTimeSpanString(toEpochMilliseconds()).toString()
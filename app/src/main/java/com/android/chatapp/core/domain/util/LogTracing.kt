package com.android.chatapp.core.domain.util

import android.util.Log

const val ERROR_TAG = "ChatError"

val Exception.logError get() = Log.e(ERROR_TAG, this.message, this)
fun logError(message: String, ex: Exception) = Log.e(ERROR_TAG, message, ex)
fun logError(message: String) = Log.e(ERROR_TAG, message)
val <T> T.logError get() = apply { logError("$this") }
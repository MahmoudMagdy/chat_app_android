package com.android.chatapp.core.domain.util

import android.util.Log

const val ERROR_TAG = "ChatError"

fun logError(ex: Exception) = Log.e(ERROR_TAG, ex.message, ex)
fun logError(message: String) = Log.e(ERROR_TAG, message)
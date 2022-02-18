package com.android.chatapp.core.presentation.util

import android.content.Context

private const val DIP = 160.0

fun Context.pixelFromDp(dp: Int): Float = (dp * (resources.displayMetrics.densityDpi / DIP)).toFloat()
fun Context.dpFromPixel(pixel: Float): Float = (pixel * (DIP / resources.displayMetrics.densityDpi)).toFloat()
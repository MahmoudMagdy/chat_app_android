package com.android.chatapp.core.domain.util

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


object AppFilesUtil {
    private const val GENERAL_DATE_FORMAT = "dd-MM-yyyy HH:mm:ss.SSS"
    private const val IMAGE_NAME_DATE_FORMAT = "dd_MM_yyyy_HH_mm_ss_SSS"
    private const val USER_PP_NAME_DATE_FORMAT = "dd_MM_yy_ss_SSS"
    private const val GENERAL_TIME_FORMAT = "HH:mm"
    private const val MINUTES_IN_HOUR = 60
    private const val SECONDS_IN_MINUTE = MINUTES_IN_HOUR
    private const val BYTE = 1024f

    fun formatSize(bytes: Long) = if (bytes < BYTE * BYTE)
        "${"%.2f".format(bytes / BYTE)} KB"
    else
        "${"%.2f".format(bytes / (BYTE * BYTE))} MB"

    fun formatMilliseconds(duration: Long) =
            String.format(Locale.US, "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(duration),
                    TimeUnit.MILLISECONDS.toMinutes(duration) % MINUTES_IN_HOUR,
                    TimeUnit.MILLISECONDS.toSeconds(duration) % SECONDS_IN_MINUTE).removePrefix("00:").trim()

    val nameSuffix: String get() = SimpleDateFormat(IMAGE_NAME_DATE_FORMAT, Locale.US).format(Date())
    val ppNameSuffix: String get() = SimpleDateFormat(USER_PP_NAME_DATE_FORMAT, Locale.US).format(Date())
    fun getTimeInGeneralFormat(date: Date): String = SimpleDateFormat(GENERAL_TIME_FORMAT, Locale.US).format(date)
}
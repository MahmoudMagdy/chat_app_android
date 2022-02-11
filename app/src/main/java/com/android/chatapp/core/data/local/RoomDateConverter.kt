package com.android.chatapp.core.data.local

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

object RoomDateConverter {
    @TypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?): Instant? = value?.let { Instant.fromEpochMilliseconds(it) }

    @TypeConverter
    @JvmStatic
    fun instantToTimestamp(date: Instant?): Long? = date?.toEpochMilliseconds()

    @TypeConverter
    @JvmStatic
    fun fromISOString(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    @JvmStatic
    fun dateToISOString(date: LocalDate?): String? = date?.toString()
}
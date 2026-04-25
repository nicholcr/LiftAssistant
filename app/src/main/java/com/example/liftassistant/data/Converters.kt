package com.example.liftassistant.data

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    //Date <---> Long
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}
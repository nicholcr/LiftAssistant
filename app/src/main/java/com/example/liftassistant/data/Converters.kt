package com.example.liftassistant.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import java.util.Date

class Converters {
    private val gson = Gson()

    //Date <---> Long
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}
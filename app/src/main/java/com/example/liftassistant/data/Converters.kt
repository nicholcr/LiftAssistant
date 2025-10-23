package com.example.liftassistant.data

import android.R.attr.value
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {
    private val gson = Gson()

    // DATE CONVERTERS
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // EXERCISES CONVERTERS
    @TypeConverter
    fun fromExerciseList(value: List<Exercise>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toExerciseList(value: String?): List<Exercise>? {
        if (value == null) return emptyList()
        val listType = object : TypeToken<List<Exercise>>() {}.type
        return gson.fromJson(value, listType)
    }

    // SETS CONVERTERS
    @TypeConverter
    fun fromSetList(sets: List<Set>?): String? {
        return gson.toJson(sets)
    }
    @TypeConverter
    fun toSetList(data: String?): List<Set>? {
        if (data == null) return emptyList()
        val listType = object : TypeToken<List<Set>>() {}.type
        return gson.fromJson(data, listType)
    }
}
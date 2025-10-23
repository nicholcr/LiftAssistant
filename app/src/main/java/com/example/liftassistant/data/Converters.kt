package com.example.liftassistant.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromExerciseList(value: List<Exercise>?): String? = gson.toJson(value)

    @TypeConverter
    fun toExerciseList(value: String?): List<Exercise>? {
        if (value == null) return emptyList()
        val listType = object : TypeToken<List<Exercise>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromSetList(value: List<WorkoutSet>?): String? = gson.toJson(value)

    @TypeConverter
    fun toSetList(value: String?): List<WorkoutSet>? {
        if (value == null) return emptyList()
        val listType = object : TypeToken<List<WorkoutSet>>() {}.type
        return gson.fromJson(value, listType)
    }
}
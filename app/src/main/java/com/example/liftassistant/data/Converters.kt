package com.example.liftassistant.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromSetList(sets: List<Set>): String {
        return gson.toJson(sets)
    }

    @TypeConverter
    fun toSetList(data: String): List<Set> {
        val listType = object : TypeToken<List<Set>>() {}.type
        return gson.fromJson(data, listType)
    }
}
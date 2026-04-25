package com.example.liftassistant.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val isBodyweight: Boolean = false,
    var prWeight: Float = 0f,
    var latestWeight: Float = 0f,
)
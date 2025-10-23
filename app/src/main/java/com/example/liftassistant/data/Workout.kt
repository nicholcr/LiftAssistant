package com.example.liftassistant.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val date: Date = Date(),
    val duration: Long = 0L,
    var exercises: List<Exercise> = emptyList(),
    val routineUsedId: String? = null
) {
}
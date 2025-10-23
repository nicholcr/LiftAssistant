package com.example.liftassistant.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_routines")
data class WorkoutRoutine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val exercises: List<Exercise> = emptyList()
) {
}
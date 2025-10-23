package com.example.liftassistant.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_exercises")
data class WorkoutExercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val workoutId: Int,
    val exerciseId: Int,
    val workoutSets: List<WorkoutSet> = emptyList()
)

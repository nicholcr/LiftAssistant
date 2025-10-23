package com.example.liftassistant.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "exercise_history_items")
data class ExerciseHistoryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val exerciseId: Int,
    val workoutId: Int,
    val dateCompleted: Date,
    val workoutSets: List<WorkoutSet> = emptyList()
)

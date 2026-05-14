package com.example.liftassistant.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_sets",
    foreignKeys = [ForeignKey(
        entity = WorkoutExercise::class,
        parentColumns = ["id"],
        childColumns = ["workoutExerciseId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["workoutExerciseId"])]
)
data class WorkoutSet(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val workoutExerciseId: Int,
    val order: Int,
    val reps: Int,
    val weight: Float,
    val isAmrap: Boolean = false
)

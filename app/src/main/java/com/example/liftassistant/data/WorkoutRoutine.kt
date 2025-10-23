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

@Entity(
    tableName = "routine_exercises",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = WorkoutRoutine::class,
            parentColumns = ["id"],
            childColumns = ["routineId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        ),
        androidx.room.ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index("routineId"), androidx.room.Index("exerciseId")]
)
data class RoutineExercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val routineId: Int,
    val exerciseId: Int
)
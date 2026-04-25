package com.example.liftassistant.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "routine_slots",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutRoutine::class,
            parentColumns = ["id"],
            childColumns = ["routineId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["fixedExerciseId"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class RoutineSlot(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val routineId: Int,
    val order: Int,
    val setScheme: String,
    val fixedExerciseId: Int? = null,
    val categoryLabel: String? = null,
    val note: String? = null
)

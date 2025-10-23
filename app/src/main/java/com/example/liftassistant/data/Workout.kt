package com.example.liftassistant.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "workouts",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = WorkoutRoutine::class,
            parentColumns = ["id"],
            childColumns = ["routineId"],
            onDelete = androidx.room.ForeignKey.SET_NULL
        )
    ],
    indices = [androidx.room.Index("routineUsedId")]
)
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val date: Date,
    val duration: Long,
    val routineUsedId: Int? = null
) {
}
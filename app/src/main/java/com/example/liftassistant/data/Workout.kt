package com.example.liftassistant.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "workouts",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutRoutine::class,
            parentColumns = ["id"],
            childColumns = ["routineUsedId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val date: Date = Date(),
    val endTime: Date? = null,
    val routineUsedId: Int? = null
) {
    val duration: Long?
        get() = endTime?.let { it.time - date.time }

    val isInProgress: Boolean
        get() = endTime == null
}
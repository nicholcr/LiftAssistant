package com.example.liftassistant.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Date

@Entity(
    tableName = "exercise_history_items",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        ),
        androidx.room.ForeignKey(
            entity = Workout::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index("exerciseId"), androidx.room.Index("workoutId")]
)
data class ExerciseHistoryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val exerciseId: Int,
    val workoutId: Int,
    val dateCompleted: Date,
    @TypeConverters(Converters::class)
    val sets: List<Set>
)

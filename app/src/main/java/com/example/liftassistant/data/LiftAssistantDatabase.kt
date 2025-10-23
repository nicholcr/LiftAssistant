package com.example.liftassistant.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.liftassistant.data.daos.ExerciseDao

@Database(entities = [Exercise::class, ExerciseHistoryItem::class, Workout::class,
    WorkoutExercise::class, WorkoutRoutine::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class LiftAssistantDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao

    companion object {
        @Volatile
        private var Instance: LiftAssistantDatabase? = null

        fun getDatabase(context: Context): LiftAssistantDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, LiftAssistantDatabase::class.java, "lift_assistant_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
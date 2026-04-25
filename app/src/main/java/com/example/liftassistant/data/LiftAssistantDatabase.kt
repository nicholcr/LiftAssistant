package com.example.liftassistant.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.liftassistant.data.daos.ExerciseDao
import com.example.liftassistant.data.daos.RoutineSlotDao
import com.example.liftassistant.data.daos.WorkoutDao
import com.example.liftassistant.data.daos.WorkoutExerciseDao
import com.example.liftassistant.data.daos.WorkoutRoutineDao
import com.example.liftassistant.data.daos.WorkoutSetDao


@Database(
    entities = [
        Exercise::class,
        WorkoutRoutine::class,
        RoutineSlot::class,
        Workout::class,
        WorkoutExercise::class,
        WorkoutSet::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LiftAssistantDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutRoutineDao(): WorkoutRoutineDao
    abstract fun routineSlotDao(): RoutineSlotDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutExerciseDao(): WorkoutExerciseDao
    abstract fun workoutSetDao(): WorkoutSetDao

    companion object {
        @Volatile
        private var Instance: LiftAssistantDatabase? = null

        fun getDatabase(context: Context): LiftAssistantDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    LiftAssistantDatabase::class.java,
                    "lift_assistant_database"
                )
                    .addCallback(object : Callback() {
                        override fun onOpen(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                            super.onOpen(db)
                            db.execSQL("PRAGMA foreign_keys = ON")
                        }
                    })
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
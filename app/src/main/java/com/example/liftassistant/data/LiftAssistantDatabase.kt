package com.example.liftassistant.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.liftassistant.data.daos.CategoryDao
import com.example.liftassistant.data.daos.ExerciseDao
import com.example.liftassistant.data.daos.RoutineSlotDao
import com.example.liftassistant.data.daos.WorkoutDao
import com.example.liftassistant.data.daos.WorkoutExerciseDao
import com.example.liftassistant.data.daos.WorkoutRoutineDao
import com.example.liftassistant.data.daos.WorkoutSetDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Database(
    entities = [
        Category::class,
        Exercise::class,
        ExerciseCategory::class,
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

    abstract fun categoryDao(): CategoryDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutRoutineDao(): WorkoutRoutineDao
    abstract fun routineSlotDao(): RoutineSlotDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutExerciseDao(): WorkoutExerciseDao
    abstract fun workoutSetDao(): WorkoutSetDao

    companion object {
        @Volatile
        private var Instance: LiftAssistantDatabase? = null

        private val defaultCategories = listOf(
            "Push", "Pull", "Legs", "Core",
            "Chest", "Back", "Shoulders",
            "Biceps", "Triceps",
            "Quads", "Hamstrings", "Glutes", "Calves",
            "Abs", "Obliques",
            "Cardio", "Favorites"
        )

        fun getDatabase(context: Context): LiftAssistantDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    LiftAssistantDatabase::class.java,
                    "lift_assistant_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            db.execSQL("PRAGMA foreign_keys = ON")
                            CoroutineScope(Dispatchers.IO).launch {
                                Instance?.categoryDao()?.insertAll(
                                    defaultCategories.map { Category(name = it) }
                                )
                            }
                        }
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
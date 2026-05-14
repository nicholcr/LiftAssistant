package com.example.liftassistant.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
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
    version = 3,
    exportSchema = true
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

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
            CREATE TABLE `workout_exercises_new` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `workoutId` INTEGER NOT NULL,
                `exerciseId` INTEGER,
                `order` INTEGER NOT NULL,
                `routineSlotId` INTEGER,
                FOREIGN KEY(`workoutId`) REFERENCES `workouts`(`id`) ON DELETE CASCADE,
                FOREIGN KEY(`exerciseId`) REFERENCES `exercises`(`id`) ON DELETE RESTRICT,
                FOREIGN KEY(`routineSlotId`) REFERENCES `routine_slots`(`id`) ON DELETE SET NULL
            )
        """)
                db.execSQL("""
            INSERT INTO `workout_exercises_new` 
            SELECT `id`, `workoutId`, `exerciseId`, `order`, `routineSlotId` 
            FROM `workout_exercises`
        """)
                db.execSQL("DROP TABLE `workout_exercises`")
                db.execSQL("ALTER TABLE `workout_exercises_new` RENAME TO `workout_exercises`")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_exercises_workoutId` ON `workout_exercises` (`workoutId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_exercises_exerciseId` ON `workout_exercises` (`exerciseId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_exercises_routineSlotId` ON `workout_exercises` (`routineSlotId`)")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_workout_sets_workoutExerciseId` ON `workout_sets` (`workoutExerciseId`)"
                )
            }
        }

        fun getDatabase(context: Context): LiftAssistantDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    LiftAssistantDatabase::class.java,
                    "lift_assistant_database"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3
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
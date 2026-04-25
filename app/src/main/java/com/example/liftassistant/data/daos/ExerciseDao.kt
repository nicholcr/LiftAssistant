package com.example.liftassistant.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.liftassistant.data.Exercise
import com.example.liftassistant.data.ExerciseCategory
import com.example.liftassistant.data.ExerciseWithCategories
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(exercise: Exercise): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertExerciseCategory(exerciseCategory: ExerciseCategory)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAllExerciseCategories(exerciseCategories: List<ExerciseCategory>)

    @Update
    suspend fun update(exercise: Exercise)

    @Delete
    suspend fun delete(exercise: Exercise)

    @Query("DELETE FROM exercise_categories WHERE exerciseId = :exerciseId")
    suspend fun deleteAllCategoriesForExercise(exerciseId: Int)

    @Transaction
    @Query("SELECT * from exercises WHERE id = :id")
    fun getExerciseWithCategories(id: Int): Flow<ExerciseWithCategories?>

    @Transaction
    @Query("SELECT * from exercises ORDER BY name ASC")
    fun getAllExercisesWithCategories(): Flow<List<ExerciseWithCategories>>

    @Transaction
    @Query("""
        SELECT DISTINCT e.* FROM exercises e
        INNER JOIN exercise_categories ec ON e.id = ec.exerciseId
        INNER JOIN categories c ON ec.categoryId = c.id
        WHERE c.name = :categoryName
        ORDER BY e.name ASC
    """)
    fun getExercisesWithCategoriesByCategoryName(categoryName: String): Flow<List<ExerciseWithCategories>>

    @Query("SELECT MAX(weight) FROM workout_sets WHERE workoutExerciseId IN (SELECT id FROM workout_exercises WHERE exerciseId = :exerciseId)")
    fun getPrWeightForExercise(exerciseId: Int): Flow<Float?>

    @Query("""
        SELECT ws.weight FROM workout_sets ws
        INNER JOIN workout_exercises we ON ws.workoutExerciseId = we.id
        INNER JOIN workouts w ON we.workoutId = w.id
        WHERE we.exerciseId = :exerciseId
        ORDER BY w.date DESC, ws.`order` DESC
        LIMIT 1
    """)
    fun getLatestWeightForExercise(exerciseId: Int): Flow<Float?>
}
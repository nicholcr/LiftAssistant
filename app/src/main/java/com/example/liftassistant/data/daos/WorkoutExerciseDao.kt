package com.example.liftassistant.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.liftassistant.data.WorkoutExercise
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutExerciseDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(workoutExercise: WorkoutExercise): Long

    @Update
    suspend fun update(workoutExercise: WorkoutExercise)

    @Delete
    suspend fun delete(workoutExercise: WorkoutExercise)

    @Query("SELECT * FROM workout_exercises WHERE id = :id")
    fun getWorkoutExercise(id: Int): Flow<WorkoutExercise?>

    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY 'order' ASC")
    fun getExercisesForWorkout(workoutId: Int): Flow<List<WorkoutExercise>>

    @Query("SELECT * FROM workout_exercises WHERE exerciseId = :exerciseId ORDER BY workoutId DESC")
    fun getWorkoutsForExercise(exerciseId: Int): Flow<List<WorkoutExercise>>
}
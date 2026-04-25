package com.example.liftassistant.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.liftassistant.data.WorkoutSet
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSetDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(workoutSet: WorkoutSet)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(workoutSets: List<WorkoutSet>)

    @Update
    suspend fun update(workoutSet: WorkoutSet)

    @Delete
    suspend fun delete(workoutSet: WorkoutSet)

    @Query("SELECT * FROM workout_sets WHERE workoutExerciseId = :workoutExerciseId ORDER BY 'order' ASC")
    fun getSetsForWorkoutExercise(workoutExerciseId: Int): Flow<List<WorkoutSet>>

    @Query("SELECT MAX(weight) FROM workout_sets WHERE workoutExerciseId IN (SELECT id FROM workout_exercises WHERE exerciseId = :exerciseId)")
    fun getPrWeightForExercise(exerciseId: Int): Flow<Float?>

    @Query("""
        SELECT ws.weight FROM workout_sets ws
        INNER JOIN workout_exercises we ON ws.workoutExerciseId = we.id
        INNER JOIN workouts w ON we.workoutId = w.id
        WHERE we.exerciseId = :exerciseId
        ORDER BY w.date DESC, ws.'order' DESC
        LIMIT 1
    """)
    fun getLatestWeightForExercise(exerciseId: Int): Flow<Float?>
}
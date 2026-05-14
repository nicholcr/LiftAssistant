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
    suspend fun insert(workoutSet: WorkoutSet): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(workoutSets: List<WorkoutSet>)

    @Update
    suspend fun update(workoutSet: WorkoutSet)

    @Delete
    suspend fun delete(workoutSet: WorkoutSet)

    @Query("SELECT * FROM workout_sets WHERE workoutExerciseId = :workoutExerciseId ORDER BY `order` ASC")
    fun getSetsForWorkoutExercise(workoutExerciseId: Int): Flow<List<WorkoutSet>>
}
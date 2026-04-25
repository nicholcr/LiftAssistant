package com.example.liftassistant.data.daos

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.liftassistant.data.WorkoutRoutine
import kotlinx.coroutines.flow.Flow

interface WorkoutRoutineDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(workoutRoutine: WorkoutRoutine): Long

    @Update
    suspend fun update(workoutRoutine: WorkoutRoutine)

    @Delete
    suspend fun delete(workoutRoutine: WorkoutRoutine)

    @Query("SELECT * from workout_routines WHERE id = :id")
    fun getWorkoutRoutine(id: Int): Flow<WorkoutRoutine>

    @Query("SELECT * FROM workout_routines ORDER BY name ASC")
    fun getAllWorkoutRoutine(): Flow<List<WorkoutRoutine>>
}
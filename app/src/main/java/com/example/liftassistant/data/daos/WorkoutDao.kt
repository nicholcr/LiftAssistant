package com.example.liftassistant.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.liftassistant.data.Workout
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(workout: Workout): Long

    @Update
    suspend fun update(workout: Workout)

    @Delete
    suspend fun delete(workout: Workout)

    @Query("SELECT * from workouts WHERE id = :id")
    fun getWorkout(id: Int): Flow<Workout>

    @Query("SELECT * FROM workouts ORDER BY date DESC")
    fun getAllWorkouts(): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE routineUsedId = :routineId ORDER BY date DESC")
    fun getWorkoutsForRoutine(routineId: Int): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE endTime IS NULL LIMIT 1")
    fun getInProgressWorkout(): Flow<Workout?>
}
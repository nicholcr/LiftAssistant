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
    // Workouts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workout: Workout)

    @Update
    suspend fun update(workout: Workout)

    @Delete
    suspend fun delete(workout: Workout)

    @Query("SELECT * from workouts WHERE id = :id")
    fun getWorkout(id: Int): Flow<Workout>

    @Query("SELECT * FROM workouts ORDER BY name ASC")
    fun getAllWorkout(): Flow<List<Workout>>
}
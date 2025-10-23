package com.example.liftassistant.data.repos

import com.example.liftassistant.data.Workout
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    /**
     * Retrieve all the workouts from the the given data source.
     */
    fun getAllWorkoutStream(): Flow<List<Workout>>

    /**
     * Retrieve an workouts from the given data source that matches with the [id].
     */
    fun getWorkoutStream(id: Int): Flow<Workout?>

    /**
     * Insert workouts in the data source
     */
    suspend fun insertWorkout(workout: Workout)

    /**
     * Delete workouts from the data source
     */
    suspend fun deleteWorkout(workout: Workout)

    /**
     * Update workouts in the data source
     */
    suspend fun updateWorkout(workout: Workout)
}
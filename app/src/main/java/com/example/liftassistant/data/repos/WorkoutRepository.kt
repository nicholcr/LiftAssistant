package com.example.liftassistant.data.repos

import com.example.liftassistant.data.Workout
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun getAllWorkoutStream(): Flow<List<Workout>>
    fun getWorkoutStream(id: Int): Flow<Workout?>
    fun getWorkoutsForRoutineStream(routineId: Int): Flow<List<Workout>>
    suspend fun insertWorkout(workout: Workout): Long
    suspend fun deleteWorkout(workout: Workout)
    suspend fun updateWorkout(workout: Workout)
}
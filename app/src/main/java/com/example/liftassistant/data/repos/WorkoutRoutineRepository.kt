package com.example.liftassistant.data.repos

import com.example.liftassistant.data.WorkoutRoutine
import kotlinx.coroutines.flow.Flow

interface WorkoutRoutineRepository {
    fun getAllWorkoutRoutineStream(): Flow<List<WorkoutRoutine>>
    fun getWorkoutRoutineStream(id: Int): Flow<WorkoutRoutine?>
    suspend fun insertWorkoutRoutine(workoutRoutine: WorkoutRoutine): Long
    suspend fun deleteWorkoutRoutine(workoutRoutine: WorkoutRoutine)
    suspend fun updateWorkoutRoutine(workoutRoutine: WorkoutRoutine)
}
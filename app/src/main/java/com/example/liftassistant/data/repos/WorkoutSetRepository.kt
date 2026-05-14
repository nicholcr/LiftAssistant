package com.example.liftassistant.data.repos

import com.example.liftassistant.data.WorkoutSet
import kotlinx.coroutines.flow.Flow

interface WorkoutSetRepository {
    fun getSetsForWorkoutExerciseStream(workoutExerciseId: Int): Flow<List<WorkoutSet>>
    suspend fun insertWorkoutSet(workoutSet: WorkoutSet): Long
    suspend fun insertAllWorkoutSets(workoutSets: List<WorkoutSet>)
    suspend fun updateWorkoutSet(workoutSet: WorkoutSet)
    suspend fun deleteWorkoutSet(workoutSet: WorkoutSet)
}
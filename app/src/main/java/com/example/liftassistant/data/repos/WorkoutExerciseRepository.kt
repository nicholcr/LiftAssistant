package com.example.liftassistant.data.repos

import com.example.liftassistant.data.WorkoutExercise
import kotlinx.coroutines.flow.Flow

interface WorkoutExerciseRepository {
    fun getExercisesForWorkoutStream(workoutId: Int): Flow<List<WorkoutExercise>>
    fun getWorkoutExerciseStream(id: Int): Flow<WorkoutExercise?>
    fun getWorkoutsForExerciseStream(exerciseId: Int): Flow<List<WorkoutExercise>>
    suspend fun insertWorkoutExercise(workoutExercise: WorkoutExercise): Long
    suspend fun updateWorkoutExercise(workoutExercise: WorkoutExercise)
    suspend fun deleteWorkoutExercise(workoutExercise: WorkoutExercise)
}
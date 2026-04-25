package com.example.liftassistant.data.repos

import com.example.liftassistant.data.Exercise
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun getAllExercisesStream(): Flow<List<Exercise>>
    fun getExerciseStream(id: Int): Flow<Exercise?>
    fun getExercisesByCategoryStream(category: String): Flow<List<Exercise>>
    suspend fun insertExercise(exercise: Exercise)
    suspend fun deleteExercise(exercise: Exercise)
    suspend fun updateExercise(exercise: Exercise)
}
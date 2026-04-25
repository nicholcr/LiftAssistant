package com.example.liftassistant.data.repos

import com.example.liftassistant.data.Exercise
import com.example.liftassistant.data.ExerciseCategory
import com.example.liftassistant.data.ExerciseWithCategories
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun getAllExercisesWithCategoriesStream(): Flow<List<ExerciseWithCategories>>
    fun getExerciseWithCategoriesStream(id: Int): Flow<ExerciseWithCategories?>
    fun getExercisesWithCategoriesByCategoryNameStream(categoryName: String): Flow<List<ExerciseWithCategories>>
    fun getPrWeightForExerciseStream(exerciseId: Int): Flow<Float?>
    fun getLatestWeightForExerciseStream(exerciseId: Int): Flow<Float?>
    suspend fun insertExercise(exercise: Exercise): Long
    suspend fun insertExerciseCategory(exerciseCategory: ExerciseCategory)
    suspend fun insertAllExerciseCategories(exerciseCategories: List<ExerciseCategory>)
    suspend fun deleteAllCategoriesForExercise(exerciseId: Int)
    suspend fun updateExercise(exercise: Exercise)
    suspend fun deleteExercise(exercise: Exercise)
}
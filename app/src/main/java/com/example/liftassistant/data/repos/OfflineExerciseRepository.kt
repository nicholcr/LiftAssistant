package com.example.liftassistant.data.repos

import com.example.liftassistant.data.Exercise
import com.example.liftassistant.data.ExerciseCategory
import com.example.liftassistant.data.ExerciseWithCategories
import com.example.liftassistant.data.daos.ExerciseDao
import kotlinx.coroutines.flow.Flow

class OfflineExerciseRepository(
    private val exerciseDao: ExerciseDao
) : ExerciseRepository {
    override fun getAllExercisesWithCategoriesStream(): Flow<List<ExerciseWithCategories>> =
        exerciseDao.getAllExercisesWithCategories()

    override fun getExerciseWithCategoriesStream(id: Int): Flow<ExerciseWithCategories?> =
        exerciseDao.getExerciseWithCategories(id)

    override fun getExercisesWithCategoriesByCategoryNameStream(categoryName: String): Flow<List<ExerciseWithCategories>> =
        exerciseDao.getExercisesWithCategoriesByCategoryName(categoryName)

    override fun getPrWeightForExerciseStream(exerciseId: Int): Flow<Float?> =
        exerciseDao.getPrWeightForExercise(exerciseId)

    override fun getLatestWeightForExerciseStream(exerciseId: Int): Flow<Float?> =
        exerciseDao.getLatestWeightForExercise(exerciseId)

    override suspend fun insertExercise(exercise: Exercise): Long =
        exerciseDao.insert(exercise)

    override suspend fun insertExerciseCategory(exerciseCategory: ExerciseCategory) =
        exerciseDao.insertExerciseCategory(exerciseCategory)

    override suspend fun insertAllExerciseCategories(exerciseCategories: List<ExerciseCategory>) =
        exerciseDao.insertAllExerciseCategories(exerciseCategories)

    override suspend fun deleteAllCategoriesForExercise(exerciseId: Int) =
        exerciseDao.deleteAllCategoriesForExercise(exerciseId)

    override suspend fun updateExercise(exercise: Exercise) =
        exerciseDao.update(exercise)

    override suspend fun deleteExercise(exercise: Exercise) =
        exerciseDao.delete(exercise)
}
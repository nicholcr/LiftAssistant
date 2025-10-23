package com.example.liftassistant.data.repos

import com.example.liftassistant.data.Exercise
import com.example.liftassistant.data.daos.ExerciseDao
import kotlinx.coroutines.flow.Flow

class OfflineExerciseRepository(private val exerciseDao: ExerciseDao) : ExerciseRepository {
    override fun getAllExerciseStream(): Flow<List<Exercise>> = exerciseDao.getAllExercises()

    override fun getExerciseStream(id: Int): Flow<Exercise?> = exerciseDao.getExercise(id)

    override suspend fun insertExercise(exercise: Exercise) = exerciseDao.insert(exercise)

    override suspend fun deleteExercise(exercise: Exercise) = exerciseDao.delete(exercise)

    override suspend fun updateExercise(exercise: Exercise) = exerciseDao.update(exercise)
}
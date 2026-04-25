package com.example.liftassistant.data.repos

import com.example.liftassistant.data.WorkoutSet
import com.example.liftassistant.data.daos.WorkoutSetDao
import kotlinx.coroutines.flow.Flow

class OfflineWorkoutSetRepository(
    private val workoutSetDao: WorkoutSetDao
) : WorkoutSetRepository {
    override fun getSetsForWorkoutExerciseStream(workoutExerciseId: Int): Flow<List<WorkoutSet>> =
        workoutSetDao.getSetsForWorkoutExercise(workoutExerciseId)

    override fun getPrWeightForExerciseStream(exerciseId: Int): Flow<Float?> =
        workoutSetDao.getPrWeightForExercise(exerciseId)

    override fun getLatestWeightForExerciseStream(exerciseId: Int): Flow<Float?> =
        workoutSetDao.getLatestWeightForExercise(exerciseId)

    override suspend fun insertWorkoutSet(workoutSet: WorkoutSet) =
        workoutSetDao.insert(workoutSet)

    override suspend fun insertAllWorkoutSets(workoutSets: List<WorkoutSet>) =
        workoutSetDao.insertAll(workoutSets)

    override suspend fun updateWorkoutSet(workoutSet: WorkoutSet) =
        workoutSetDao.update(workoutSet)

    override suspend fun deleteWorkoutSet(workoutSet: WorkoutSet) =
        workoutSetDao.delete(workoutSet)
}
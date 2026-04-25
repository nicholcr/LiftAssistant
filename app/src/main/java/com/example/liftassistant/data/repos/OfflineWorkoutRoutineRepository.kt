package com.example.liftassistant.data.repos

import com.example.liftassistant.data.WorkoutRoutine
import com.example.liftassistant.data.daos.WorkoutRoutineDao
import kotlinx.coroutines.flow.Flow

class OfflineWorkoutRoutineRepository(
    private val workoutRoutineDao: WorkoutRoutineDao
) : WorkoutRoutineRepository {
    override fun getAllWorkoutRoutineStream(): Flow<List<WorkoutRoutine>> =
        workoutRoutineDao.getAllWorkoutRoutine()

    override fun getWorkoutRoutineStream(id: Int): Flow<WorkoutRoutine?> =
        workoutRoutineDao.getWorkoutRoutine(id)

    override suspend fun insertWorkoutRoutine(workoutRoutine: WorkoutRoutine): Long =
        workoutRoutineDao.insert(workoutRoutine)

    override suspend fun deleteWorkoutRoutine(workoutRoutine: WorkoutRoutine) =
        workoutRoutineDao.delete(workoutRoutine)

    override suspend fun updateWorkoutRoutine(workoutRoutine: WorkoutRoutine) =
        workoutRoutineDao.update(workoutRoutine)
}
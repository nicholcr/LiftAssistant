package com.example.liftassistant.data.repos

import com.example.liftassistant.data.Workout
import com.example.liftassistant.data.daos.WorkoutDao
import kotlinx.coroutines.flow.Flow

class OfflineWorkoutRepository(private val workoutDao: WorkoutDao) : WorkoutRepository {
    override fun getAllWorkoutStream(): Flow<List<Workout>> =
        workoutDao.getAllWorkouts()
    override fun getWorkoutStream(id: Int): Flow<Workout?> =
        workoutDao.getWorkout(id)
    override fun getWorkoutsForRoutineStream(routineId: Int): Flow<List<Workout>> =
        workoutDao.getWorkoutsForRoutine(routineId)
    override suspend fun insertWorkout(workout: Workout): Long =
        workoutDao.insert(workout)
    override suspend fun deleteWorkout(workout: Workout) =
        workoutDao.delete(workout)
    override suspend fun updateWorkout(workout: Workout) =
        workoutDao.update(workout)
}
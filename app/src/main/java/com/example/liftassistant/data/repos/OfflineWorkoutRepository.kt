package com.example.liftassistant.data.repos

import com.example.liftassistant.data.Workout
import com.example.liftassistant.data.daos.WorkoutDao
import kotlinx.coroutines.flow.Flow

class OfflineWorkoutRepository(private val workoutDao: WorkoutDao) : WorkoutRepository {
    override fun getAllWorkoutStream(): Flow<List<Workout>> = workoutDao.getAllWorkout()

    override fun getWorkoutStream(id: Int): Flow<Workout?> = workoutDao.getWorkout(id)

    override suspend fun insertWorkout(workout: Workout) = workoutDao.insert(workout)

    override suspend fun deleteWorkout(workout: Workout) = workoutDao.delete(workout)

    override suspend fun updateWorkout(workout: Workout) = workoutDao.update(workout)
}
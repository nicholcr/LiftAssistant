package com.example.liftassistant.data.repos

import com.example.liftassistant.data.WorkoutExercise
import com.example.liftassistant.data.daos.WorkoutExerciseDao
import kotlinx.coroutines.flow.Flow

class OfflineWorkoutExerciseRepository(
    private val workoutExerciseDao: WorkoutExerciseDao
) : WorkoutExerciseRepository {
    override fun getExercisesForWorkoutStream(workoutId: Int): Flow<List<WorkoutExercise>> =
        workoutExerciseDao.getExercisesForWorkout(workoutId)

    override fun getWorkoutExerciseStream(id: Int): Flow<WorkoutExercise?> =
        workoutExerciseDao.getWorkoutExercise(id)

    override fun getWorkoutsForExerciseStream(exerciseId: Int): Flow<List<WorkoutExercise>> =
        workoutExerciseDao.getWorkoutsForExercise(exerciseId)

    override suspend fun insertWorkoutExercise(workoutExercise: WorkoutExercise): Long =
        workoutExerciseDao.insert(workoutExercise)

    override suspend fun updateExerciseId(workoutExerciseId: Int, exerciseId: Int) =
        workoutExerciseDao.updateExerciseId(workoutExerciseId, exerciseId)

    override suspend fun updateWorkoutExercise(workoutExercise: WorkoutExercise) =
        workoutExerciseDao.update(workoutExercise)

    override suspend fun deleteWorkoutExercise(workoutExercise: WorkoutExercise) =
        workoutExerciseDao.delete(workoutExercise)
}
package com.example.liftassistant.data.repos

import com.example.liftassistant.data.WorkoutRoutine
import kotlinx.coroutines.flow.Flow

interface WorkoutRoutineRepository {
    /**
     * Retrieve all the workout routines from the the given data source.
     */
    fun getAllWorkoutRoutineStream(): Flow<List<WorkoutRoutine>>

    /**
     * Retrieve an workout routines from the given data source that matches with the [id].
     */
    fun getWorkoutRoutineStream(id: Int): Flow<WorkoutRoutine?>

    /**
     * Insert workout routines in the data source
     */
    suspend fun insertWorkoutRoutine(item: WorkoutRoutine)

    /**
     * Delete workout routines from the data source
     */
    suspend fun deleteWorkoutRoutine(item: WorkoutRoutine)

    /**
     * Update workout routines in the data source
     */
    suspend fun updateWorkoutRoutine(item: WorkoutRoutine)
}
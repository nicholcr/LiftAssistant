package com.example.liftassistant.data.repos

import com.example.liftassistant.data.Exercise
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllExerciseStream(): Flow<List<Exercise>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getExerciseStream(id: Int): Flow<Exercise?>

    /**
     * Insert item in the data source
     */
    suspend fun insertExercise(item: Exercise)

    /**
     * Delete item from the data source
     */
    suspend fun deleteExercise(item: Exercise)

    /**
     * Update item in the data source
     */
    suspend fun updateExercise(item: Exercise)
}
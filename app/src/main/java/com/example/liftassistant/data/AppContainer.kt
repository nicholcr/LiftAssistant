package com.example.liftassistant.data

import android.content.Context
import com.example.liftassistant.data.repos.ExerciseRepository
import com.example.liftassistant.data.repos.OfflineExerciseRepository
import com.example.liftassistant.data.repos.OfflineWorkoutRepository
import com.example.liftassistant.data.repos.OfflineWorkoutRoutineRepository
import com.example.liftassistant.data.repos.WorkoutRepository
import com.example.liftassistant.data.repos.WorkoutRoutineRepository

interface AppContainer {
    val exerciseRepository: ExerciseRepository
    val workoutRepository: WorkoutRepository
    val workoutRoutineRepository: WorkoutRoutineRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val exerciseRepository: ExerciseRepository by lazy {
        OfflineExerciseRepository(LiftAssistantDatabase.getDatabase(context).exerciseDao())
    }
    override val workoutRepository: WorkoutRepository by lazy {
        OfflineWorkoutRepository(LiftAssistantDatabase.getDatabase(context).workoutDao())
    }
    override val workoutRoutineRepository: WorkoutRoutineRepository by lazy {
        OfflineWorkoutRoutineRepository(LiftAssistantDatabase.getDatabase(context).workoutRoutineDao())
    }
}
package com.example.liftassistant.data

import android.content.Context
import com.example.liftassistant.data.repos.CategoryRepository
import com.example.liftassistant.data.repos.ExerciseRepository
import com.example.liftassistant.data.repos.OfflineCategoryRepository
import com.example.liftassistant.data.repos.OfflineExerciseRepository
import com.example.liftassistant.data.repos.OfflineRoutineSlotRepository
import com.example.liftassistant.data.repos.OfflineWorkoutExerciseRepository
import com.example.liftassistant.data.repos.OfflineWorkoutRepository
import com.example.liftassistant.data.repos.OfflineWorkoutRoutineRepository
import com.example.liftassistant.data.repos.OfflineWorkoutSetRepository
import com.example.liftassistant.data.repos.RoutineSlotRepository
import com.example.liftassistant.data.repos.WorkoutExerciseRepository
import com.example.liftassistant.data.repos.WorkoutRepository
import com.example.liftassistant.data.repos.WorkoutRoutineRepository
import com.example.liftassistant.data.repos.WorkoutSetRepository

interface AppContainer {
    val categoryRepository: CategoryRepository
    val exerciseRepository: ExerciseRepository
    val workoutRepository: WorkoutRepository
    val workoutRoutineRepository: WorkoutRoutineRepository
    val routineSlotRepository: RoutineSlotRepository
    val workoutExerciseRepository: WorkoutExerciseRepository
    val workoutSetRepository: WorkoutSetRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val categoryRepository: CategoryRepository by lazy {
        OfflineCategoryRepository(LiftAssistantDatabase.getDatabase(context).categoryDao())
    }
    override val exerciseRepository: ExerciseRepository by lazy {
        OfflineExerciseRepository(LiftAssistantDatabase.getDatabase(context).exerciseDao())
    }
    override val workoutRepository: WorkoutRepository by lazy {
        OfflineWorkoutRepository(LiftAssistantDatabase.getDatabase(context).workoutDao())
    }
    override val workoutRoutineRepository: WorkoutRoutineRepository by lazy {
        OfflineWorkoutRoutineRepository(LiftAssistantDatabase.getDatabase(context).workoutRoutineDao())
    }
    override val routineSlotRepository: RoutineSlotRepository by lazy {
        OfflineRoutineSlotRepository(LiftAssistantDatabase.getDatabase(context).routineSlotDao())
    }
    override val workoutExerciseRepository: WorkoutExerciseRepository by lazy {
        OfflineWorkoutExerciseRepository(LiftAssistantDatabase.getDatabase(context).workoutExerciseDao())
    }
    override val workoutSetRepository: WorkoutSetRepository by lazy {
        OfflineWorkoutSetRepository(LiftAssistantDatabase.getDatabase(context).workoutSetDao())
    }
}
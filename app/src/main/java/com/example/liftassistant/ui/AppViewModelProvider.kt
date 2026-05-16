package com.example.liftassistant.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.liftassistant.LiftAssistantApplication
import com.example.liftassistant.ui.exercise.AddExerciseViewModel
import com.example.liftassistant.ui.exercise.EditExerciseViewModel
import com.example.liftassistant.ui.exercise.ExerciseListViewModel
import com.example.liftassistant.ui.home.HomeViewModel
import com.example.liftassistant.ui.workout.PerformWorkoutViewModel
import com.example.liftassistant.ui.workout.WorkoutSummaryViewModel
import com.example.liftassistant.ui.workout_routine.WorkoutRoutineListViewModel
import com.example.liftassistant.ui.workout_routine.WorkoutRoutineViewModel
import com.example.liftassistant.ui.workout_routine.AddWorkoutRoutineViewModel
import com.example.liftassistant.ui.workout_routine.EditWorkoutRoutineViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {

        // Home
        initializer {
            HomeViewModel(
                liftAssistantApplication().container.workoutRepository,
                liftAssistantApplication().container.workoutRoutineRepository
            )
        }

        // Exercise
        initializer {
            ExerciseListViewModel(
                liftAssistantApplication().container.exerciseRepository,
                liftAssistantApplication().container.categoryRepository
            )
        }
        initializer {
            AddExerciseViewModel(
                liftAssistantApplication().container.exerciseRepository,
                liftAssistantApplication().container.categoryRepository,
                liftAssistantApplication().container.routineSlotRepository
            )
        }
        initializer {
            EditExerciseViewModel(
                liftAssistantApplication().container.exerciseRepository,
                liftAssistantApplication().container.categoryRepository,
                liftAssistantApplication().container.routineSlotRepository,
                this.createSavedStateHandle()
            )
        }

        // Workout Routine
        initializer {
            WorkoutRoutineListViewModel(
                liftAssistantApplication().container.workoutRoutineRepository
            )
        }
        initializer {
            WorkoutRoutineViewModel(
                liftAssistantApplication().container.workoutRoutineRepository,
                liftAssistantApplication().container.routineSlotRepository,
                liftAssistantApplication().container.exerciseRepository,
                this.createSavedStateHandle()
            )
        }
        initializer {
            AddWorkoutRoutineViewModel(
                liftAssistantApplication().container.workoutRoutineRepository,
                liftAssistantApplication().container.routineSlotRepository,
                liftAssistantApplication().container.exerciseRepository
            )
        }
        initializer {
            EditWorkoutRoutineViewModel(
                liftAssistantApplication().container.workoutRoutineRepository,
                liftAssistantApplication().container.routineSlotRepository,
                liftAssistantApplication().container.exerciseRepository,
                this.createSavedStateHandle()
            )
        }

        // Workout
        initializer {
            PerformWorkoutViewModel(
                liftAssistantApplication().container.workoutRepository,
                liftAssistantApplication().container.workoutExerciseRepository,
                liftAssistantApplication().container.workoutSetRepository,
                liftAssistantApplication().container.exerciseRepository,
                liftAssistantApplication().container.routineSlotRepository,
                liftAssistantApplication().container.workoutRoutineRepository,
                this.createSavedStateHandle()
            )
        }
        initializer {
            WorkoutSummaryViewModel(
                liftAssistantApplication().container.workoutRepository,
                liftAssistantApplication().container.workoutExerciseRepository,
                liftAssistantApplication().container.workoutSetRepository,
                liftAssistantApplication().container.exerciseRepository,
                this.createSavedStateHandle()
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [LiftAssistantApplication].
 */
fun CreationExtras.liftAssistantApplication(): LiftAssistantApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as LiftAssistantApplication)
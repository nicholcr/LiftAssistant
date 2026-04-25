package com.example.liftassistant.ui.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.liftassistant.data.repos.ExerciseRepository
import com.example.liftassistant.data.repos.RoutineSlotRepository
import com.example.liftassistant.data.repos.WorkoutExerciseRepository
import com.example.liftassistant.data.repos.WorkoutRepository
import com.example.liftassistant.data.repos.WorkoutSetRepository

class PerformWorkoutViewModel(
    private val workoutRepository: WorkoutRepository,
    private val workoutExerciseRepository: WorkoutExerciseRepository,
    private val workoutSetRepository: WorkoutSetRepository,
    private val exerciseRepository: ExerciseRepository,
    private val routineSlotRepository: RoutineSlotRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel()
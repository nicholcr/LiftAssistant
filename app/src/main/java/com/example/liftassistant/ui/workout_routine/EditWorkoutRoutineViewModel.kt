package com.example.liftassistant.ui.workout_routine

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.liftassistant.data.repos.ExerciseRepository
import com.example.liftassistant.data.repos.RoutineSlotRepository
import com.example.liftassistant.data.repos.WorkoutRoutineRepository

class EditWorkoutRoutineViewModel(
    private val workoutRoutineRepository: WorkoutRoutineRepository,
    private val routineSlotRepository: RoutineSlotRepository,
    private val exerciseRepository: ExerciseRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel()
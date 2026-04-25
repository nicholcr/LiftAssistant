package com.example.liftassistant.ui.workout_routine

import androidx.lifecycle.ViewModel
import com.example.liftassistant.data.repos.WorkoutRoutineRepository

class WorkoutRoutineListViewModel(
    private val workoutRoutineRepository: WorkoutRoutineRepository
) : ViewModel()
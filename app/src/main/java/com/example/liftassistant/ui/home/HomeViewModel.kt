package com.example.liftassistant.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liftassistant.data.Workout
import com.example.liftassistant.data.WorkoutRoutine
import com.example.liftassistant.data.repos.WorkoutRepository
import com.example.liftassistant.data.repos.WorkoutRoutineRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to retrieve all past workouts in the Room database.
 */
class HomeViewModel(
    private val workoutRepository: WorkoutRepository,
    private val workoutRoutineRepository: WorkoutRoutineRepository
) : ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val homeUiState: StateFlow<HomeUiState> = combine (
        workoutRepository.getAllWorkoutStream(),
        workoutRepository.getInProgressWorkoutStream(),
        workoutRoutineRepository.getAllWorkoutRoutineStream()
    ) { workouts, inProgressWorkout, routines ->
        HomeUiState(
            workoutList = workouts.filter { it.endTime != null },
            inProgressWorkout = inProgressWorkout,
            workoutRoutineList = routines
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = HomeUiState()
    )
}

data class HomeUiState(
    val workoutList: List<Workout> = emptyList(),
    val inProgressWorkout: Workout? = null,
    val workoutRoutineList: List<WorkoutRoutine> = emptyList()
)
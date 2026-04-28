package com.example.liftassistant.ui.workout_routine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liftassistant.data.WorkoutRoutine
import com.example.liftassistant.data.repos.WorkoutRoutineRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutRoutineListViewModel(
    private val workoutRoutineRepository: WorkoutRoutineRepository
) : ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val uiState: StateFlow<WorkoutRoutineListUiState> =
        workoutRoutineRepository.getAllWorkoutRoutineStream()
            .map { WorkoutRoutineListUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = WorkoutRoutineListUiState()
            )

    fun deleteWorkoutRoutine(workoutRoutine: WorkoutRoutine) {
        viewModelScope.launch {
            workoutRoutineRepository.deleteWorkoutRoutine(workoutRoutine)
        }
    }
}

data class WorkoutRoutineListUiState(
    val workoutRoutineList: List<WorkoutRoutine> = emptyList()
)
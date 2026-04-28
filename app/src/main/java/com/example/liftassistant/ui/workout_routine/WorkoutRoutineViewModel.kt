package com.example.liftassistant.ui.workout_routine

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liftassistant.data.Exercise
import com.example.liftassistant.data.RoutineSlot
import com.example.liftassistant.data.WorkoutRoutine
import com.example.liftassistant.data.repos.ExerciseRepository
import com.example.liftassistant.data.repos.RoutineSlotRepository
import com.example.liftassistant.data.repos.WorkoutRoutineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RoutineSlotItem(
    val slot: RoutineSlot,
    val fixedExercise: Exercise? = null
)

data class WorkoutRoutineUiState(
    val routine: WorkoutRoutine? = null,
    val slotItems: List<RoutineSlotItem> = emptyList(),
    val isLoading: Boolean = true
)

class WorkoutRoutineViewModel(
    private val workoutRoutineRepository: WorkoutRoutineRepository,
    private val routineSlotRepository: RoutineSlotRepository,
    private val exerciseRepository: ExerciseRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val routineId: Int = checkNotNull(
        savedStateHandle[WorkoutRoutineDestination.routineIdArg]
    )

    private val _uiState = MutableStateFlow(WorkoutRoutineUiState())
    val uiState: StateFlow<WorkoutRoutineUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadRoutine()
        }
    }

    private suspend fun loadRoutine() {
        val routine = workoutRoutineRepository
            .getWorkoutRoutineStream(routineId)
            .filterNotNull()
            .first()

        val slots = routineSlotRepository
            .getSlotsForRoutineStream(routineId)
            .first()

        val slotItems = slots.map { slot ->
            val fixedExercise = slot.fixedExerciseId?.let { exerciseId ->
                exerciseRepository
                    .getExerciseWithCategoriesStream(exerciseId)
                    .filterNotNull()
                    .first()
                    .exercise
            }
            RoutineSlotItem(
                slot = slot,
                fixedExercise = fixedExercise
            )
        }

        _uiState.update {
            it.copy(
                routine = routine,
                slotItems = slotItems,
                isLoading = false
            )
        }
    }
}
package com.example.liftassistant.ui.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liftassistant.data.Exercise
import com.example.liftassistant.data.repos.ExerciseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ExerciseListViewModel(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val uiState: StateFlow<ExerciseListUiState> =
        exerciseRepository.getAllExercisesStream()
            .map { ExerciseListUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ExerciseListUiState()
            )

    suspend fun deleteExercise(exercise: Exercise) =
        exerciseRepository.deleteExercise(exercise)
}

data class ExerciseListUiState(
    val exerciseList: List<Exercise> = emptyList()
)
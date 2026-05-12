package com.example.liftassistant.ui.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liftassistant.data.Exercise
import com.example.liftassistant.data.Workout
import com.example.liftassistant.data.WorkoutExercise
import com.example.liftassistant.data.WorkoutSet
import com.example.liftassistant.data.repos.ExerciseRepository
import com.example.liftassistant.data.repos.WorkoutExerciseRepository
import com.example.liftassistant.data.repos.WorkoutRepository
import com.example.liftassistant.data.repos.WorkoutSetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WorkoutSummaryExerciseItem(
    val workoutExercise: WorkoutExercise,
    val exercise: Exercise,
    val sets: List<WorkoutSet>
)

data class WorkoutSummaryUiState(
    val workout: Workout? = null,
    val exerciseItems: List<WorkoutSummaryExerciseItem> = emptyList(),
    val isLoading: Boolean = true
)

class WorkoutSummaryViewModel(
    private val workoutRepository: WorkoutRepository,
    private val workoutExerciseRepository: WorkoutExerciseRepository,
    private val workoutSetRepository: WorkoutSetRepository,
    private val exerciseRepository: ExerciseRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val workoutId: Int = checkNotNull(
        savedStateHandle[WorkoutSummaryDestination.workoutIdArg]
    )

    private val _uiState = MutableStateFlow(WorkoutSummaryUiState())
    val uiState: StateFlow<WorkoutSummaryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadWorkoutSummary()
        }
    }

    private suspend fun loadWorkoutSummary() {
        val workout = workoutRepository
            .getWorkoutStream(workoutId)
            .filterNotNull()
            .first()

        val workoutExercises = workoutExerciseRepository
            .getExercisesForWorkoutStream(workoutId)
            .first()

        val exerciseItems = workoutExercises.mapNotNull { workoutExercise ->
            val exerciseId = workoutExercise.exerciseId ?: return@mapNotNull null
            val exercise = exerciseRepository
                .getExerciseWithCategoriesStream(exerciseId)
                .filterNotNull()
                .first()
                .exercise
            val sets = workoutSetRepository
                .getSetsForWorkoutExerciseStream(workoutExercise.id)
                .first()
            WorkoutSummaryExerciseItem(
                workoutExercise = workoutExercise,
                exercise = exercise,
                sets = sets
            )
        }

        _uiState.update {
            it.copy(
                workout = workout,
                exerciseItems = exerciseItems,
                isLoading = false
            )
        }
    }

    fun deleteWorkout(onDeleted: () -> Unit) {
        viewModelScope.launch {
            _uiState.value.workout?.let { workout ->
                workoutRepository.deleteWorkout(workout)
                onDeleted()
            }
        }
    }
}
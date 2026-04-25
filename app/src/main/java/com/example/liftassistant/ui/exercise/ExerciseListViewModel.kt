package com.example.liftassistant.ui.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liftassistant.data.Category
import com.example.liftassistant.data.Exercise
import com.example.liftassistant.data.ExerciseWithCategories
import com.example.liftassistant.data.repos.CategoryRepository
import com.example.liftassistant.data.repos.ExerciseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExerciseListViewModel(
    private val exerciseRepository: ExerciseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val uiState: StateFlow<ExerciseListUiState> = combine(
        exerciseRepository.getAllExercisesWithCategoriesStream(),
        categoryRepository.getAllCategoriesStream()
    ) { exercises, categories ->
        ExerciseListUiState(
            exerciseList = exercises,
            allCategories = categories
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = ExerciseListUiState()
    )

    fun deleteExercise(exercise: Exercise) =
        viewModelScope.launch {
            exerciseRepository.deleteExercise(exercise)
        }
}

data class ExerciseListUiState(
    val exerciseList: List<ExerciseWithCategories> = emptyList(),
    val allCategories: List<Category> = emptyList()
)
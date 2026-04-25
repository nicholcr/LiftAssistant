package com.example.liftassistant.ui.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liftassistant.data.Category
import com.example.liftassistant.data.Exercise
import com.example.liftassistant.data.ExerciseCategory
import com.example.liftassistant.data.repos.CategoryRepository
import com.example.liftassistant.data.repos.ExerciseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class AddExerciseViewModel(
    private val exerciseRepository: ExerciseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    var formState by mutableStateOf(ExerciseFormState())
        private set

    val availableCategories: StateFlow<List<Category>> =
        categoryRepository.getAllCategoriesStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = emptyList()
            )

    fun updateFormState(newState: ExerciseFormState) {
        formState = newState.copy(isValid = validateInput(newState))
    }

    private fun validateInput(state: ExerciseFormState): Boolean {
        return state.name.isNotBlank()
    }

    suspend fun saveExercise() {
        if (!validateInput(formState)) return
        val exerciseId = exerciseRepository.insertExercise(
            Exercise(
                name = formState.name.trim(),
                isBodyweight = formState.isBodyweight
            )
        )
        val exerciseCategories = formState.selectedCategories.map { category ->
            ExerciseCategory(
                exerciseId = exerciseId.toInt(),
                categoryId = category.id
            )
        }
        if (exerciseCategories.isNotEmpty()) {
            exerciseRepository.insertAllExerciseCategories(exerciseCategories)
        }
    }
}

data class ExerciseFormState(
    val name: String = "",
    val isBodyweight: Boolean = false,
    val selectedCategories: List<Category> = emptyList(),
    val isValid: Boolean = false
)
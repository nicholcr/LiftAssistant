package com.example.liftassistant.ui.exercise

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liftassistant.data.Category
import com.example.liftassistant.data.Exercise
import com.example.liftassistant.data.ExerciseCategory
import com.example.liftassistant.data.repos.CategoryRepository
import com.example.liftassistant.data.repos.ExerciseRepository
import com.example.liftassistant.data.repos.RoutineSlotRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddExerciseViewModel(
    private val exerciseRepository: ExerciseRepository,
    private val categoryRepository: CategoryRepository,
    private val routineSlotRepository: RoutineSlotRepository
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

    suspend fun addCategory(name: String) {
        if (name.isBlank()) return
        categoryRepository.insertCategory(Category(name = name.trim()))
    }

    suspend fun deleteCategory(category: Category) {
        categoryRepository.deleteCategory(category)
        formState = formState.copy(
            selectedCategories = formState.selectedCategories.filter {
                it.id != category.id
            }
        )
    }

    suspend fun renameCategory(category: Category, newName: String) {
        if (newName.isBlank()) return
        val updatedCategory = category.copy(name = newName.trim())
        categoryRepository.updateCategory(updatedCategory)
        updateRoutineSlotsForRename(category.name, newName.trim())
        formState = formState.copy(
            selectedCategories = formState.selectedCategories.map {
                if (it.id == category.id) updatedCategory else it
            }
        )
    }

    private suspend fun updateRoutineSlotsForRename(oldName: String, newName: String) {
        val slots = routineSlotRepository.getAllSlotsWithCategoryLabel(oldName)
        slots.forEach { slot ->
            routineSlotRepository.updateRoutineSlot(slot.copy(categoryLabel = newName))
        }
    }

    suspend fun getExerciseCountForCategory(categoryId: Int): Int =
        categoryRepository.getExerciseCountForCategory(categoryId)
}
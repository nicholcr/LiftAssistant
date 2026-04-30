package com.example.liftassistant.ui.exercise

import com.example.liftassistant.data.Category
import com.example.liftassistant.data.Exercise
import com.example.liftassistant.data.ExerciseCategory

data class ExerciseFormState(
    val name: String = "",
    val isBodyweight: Boolean = false,
    val selectedCategories: List<Category> = emptyList(),
    val prWeight: Float = 0f,
    val latestWeight: Float = 0f,
    val isValid: Boolean = false
)

fun ExerciseFormState.toExercise(id: Int = 0): Exercise = Exercise(
    id = id,
    name = name.trim(),
    isBodyweight = isBodyweight,
    prWeight = prWeight,
    latestWeight = latestWeight
)

fun ExerciseFormState.toExerciseCategoryList(exerciseId: Int): List<ExerciseCategory> =
    selectedCategories.map { category ->
        ExerciseCategory(
            exerciseId = exerciseId,
            categoryId = category.id
        )
    }
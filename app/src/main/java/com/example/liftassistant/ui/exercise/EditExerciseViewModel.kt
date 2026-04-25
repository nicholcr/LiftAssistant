package com.example.liftassistant.ui.exercise

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.liftassistant.data.repos.CategoryRepository
import com.example.liftassistant.data.repos.ExerciseRepository

class EditExerciseViewModel(
    private val exerciseRepository: ExerciseRepository,
    private val categoryRepository: CategoryRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

}
package com.example.liftassistant.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.liftassistant.LiftAssistantApplication
import com.example.liftassistant.ui.home.HomeViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(liftAssistantApplication().container.workoutRepository)
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [LiftAssistantApplication].
 */
fun CreationExtras.liftAssistantApplication(): LiftAssistantApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as LiftAssistantApplication)
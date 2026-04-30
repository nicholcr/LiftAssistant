@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.liftassistant.ui.exercise

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.liftassistant.LiftAssistantTopAppBar
import com.example.liftassistant.R
import com.example.liftassistant.data.Category
import com.example.liftassistant.ui.AppViewModelProvider
import com.example.liftassistant.ui.navigation.NavigationDestination
import com.example.liftassistant.ui.theme.LiftAssistantTheme
import kotlinx.coroutines.launch
object EditExerciseDestination : NavigationDestination {
    override val route = "edit_exercise"
    override val titleRes = R.string.edit_exercise
    const val exerciseIdArg = "exerciseId"
    val routeWithArgs = "$route/{$exerciseIdArg}"
}

@Composable
fun EditExerciseScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditExerciseViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val coroutineScope = rememberCoroutineScope()
    val availableCategories by viewModel.availableCategories.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            LiftAssistantTopAppBar(
                title = stringResource(EditExerciseDestination.titleRes),
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        ExerciseForm(
            formState = viewModel.formState,
            availableCategories = availableCategories,
            onFormStateChange = viewModel::updateFormState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveExercise()
                    navigateBack()
                }
            },
            onAddCategory = viewModel::addCategory,
            onDeleteCategory = viewModel::deleteCategory,
            onRenameCategory = viewModel::renameCategory,
            onGetExerciseCount = viewModel::getExerciseCountForCategory,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EditExerciseScreenPreview() {
    LiftAssistantTheme {
        ExerciseForm(
            formState = ExerciseFormState(
                name = "Bench Press",
                isBodyweight = false,
                selectedCategories = listOf(
                    Category(id = 1, name = "Push"),
                    Category(id = 5, name = "Chest")
                ),
                prWeight = 185f,
                latestWeight = 175f,
                isValid = true
            ),
            availableCategories = listOf(
                Category(id = 1, name = "Push"),
                Category(id = 2, name = "Pull"),
                Category(id = 3, name = "Legs"),
                Category(id = 4, name = "Core"),
                Category(id = 5, name = "Chest"),
                Category(id = 6, name = "Biceps")
            ),
            onFormStateChange = {},
            onSaveClick = {},
            onAddCategory = {},
            onDeleteCategory = {},
            onRenameCategory = { _, _ -> },
            onGetExerciseCount = { 0 }
        )
    }
}
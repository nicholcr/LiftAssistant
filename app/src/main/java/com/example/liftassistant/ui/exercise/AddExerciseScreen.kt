package com.example.liftassistant.ui.exercise

import android.widget.CheckBox
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
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

object AddExerciseDestination : NavigationDestination {
    override val route = "add_exercise"
    override val titleRes = R.string.add_exercise
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddExerciseViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val coroutineScope = rememberCoroutineScope()
    val availableCategories by viewModel.availableCategories.collectAsState()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LiftAssistantTopAppBar(
                title = stringResource(AddExerciseDestination.titleRes),
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
                    navigateBack
                }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExerciseForm(
    formState: ExerciseFormState,
    availableCategories: List<Category>,
    onFormStateChange: (ExerciseFormState) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = formState.name,
            onValueChange = { onFormStateChange(formState.copy(name = it)) },
            label = { Text(stringResource(R.string.exercise_name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = formState.name.isBlank() && formState.name != ""
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.bodyweight_exercise),
                style = MaterialTheme.typography.bodyLarge
            )
            Checkbox(
                checked = formState.isBodyweight,
                onCheckedChange = { onFormStateChange(formState.copy(isBodyweight = it)) }
            )
        }
    }

    if (availableCategories.isNotEmpty()) {
        Text(
            text = stringResource(R.string.categories),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            availableCategories.forEach { category ->
                val isSelected = formState.selectedCategories.any { it.id == category.id }
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        val updatedCategories = if (isSelected) {
                            formState.selectedCategories.filter { it.id != category.id }
                        } else {
                            formState.selectedCategories + category
                        }
                        onFormStateChange(formState.copy(selectedCategories = updatedCategories))
                    },
                    label = { Text(category.name) }
                )
            }
        }
    }

    Button(
        onClick = onSaveClick,
        enabled = formState.isValid,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.save))
    }
}

@Preview(showBackground = true)
@Composable
private fun ExerciseFormEmptyPreview() {
    LiftAssistantTheme {
        ExerciseForm(
            formState = ExerciseFormState(),
            availableCategories = listOf(
                Category(id = 1, name = "Push"),
                Category(id = 2, name = "Pull"),
                Category(id = 3, name = "Legs"),
                Category(id = 4, name = "Core"),
                Category(id = 5, name = "Biceps"),
                Category(id = 6, name = "Triceps")
            ),
            onFormStateChange = {},
            onSaveClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExerciseFormFilledPreview() {
    LiftAssistantTheme {
        ExerciseForm(
            formState = ExerciseFormState(
                name = "Pull-up",
                isBodyweight = true,
                selectedCategories = listOf(
                    Category(id = 2, name = "Pull"),
                    Category(id = 5, name = "Biceps")
                ),
                isValid = true
            ),
            availableCategories = listOf(
                Category(id = 1, name = "Push"),
                Category(id = 2, name = "Pull"),
                Category(id = 3, name = "Legs"),
                Category(id = 4, name = "Core"),
                Category(id = 5, name = "Biceps"),
                Category(id = 6, name = "Triceps")
            ),
            onFormStateChange = {},
            onSaveClick = {}
        )
    }
}
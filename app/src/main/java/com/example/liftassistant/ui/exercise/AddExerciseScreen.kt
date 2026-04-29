@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.example.liftassistant.ui.exercise

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

@Composable
fun ExerciseForm(
    formState: ExerciseFormState,
    availableCategories: List<Category>,
    onFormStateChange: (ExerciseFormState) -> Unit,
    onSaveClick: () -> Unit,
    onAddCategory: suspend (String) -> Unit,
    onDeleteCategory: suspend (Category) -> Unit,
    onRenameCategory: suspend (Category, String) -> Unit,
    onGetExerciseCount: suspend (Int) -> Int,
    modifier: Modifier = Modifier
) {
    var showCategoryManager by remember { mutableStateOf(false) }

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

        if (availableCategories.isNotEmpty()) {
            Text(
                text = stringResource(R.string.categories),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.padding_small)
                )
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
                            onFormStateChange(
                                formState.copy(selectedCategories = updatedCategories)
                            )
                        },
                        label = { Text(category.name) }
                    )
                }
            }
        }

        TextButton(
            onClick = { showCategoryManager = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.manage_categories))
        }

        Button(
            onClick = onSaveClick,
            enabled = formState.isValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.save))
        }
    }

    if (showCategoryManager) {
        CategoryManagerDialog(
            availableCategories = availableCategories,
            onAddCategory = onAddCategory,
            onDeleteCategory = onDeleteCategory,
            onRenameCategory = onRenameCategory,
            onGetExerciseCount = onGetExerciseCount,
            onDismiss = { showCategoryManager = false }
        )
    }
}

@Composable
fun CategoryManagerDialog(
    availableCategories: List<Category>,
    onAddCategory: suspend (String) -> Unit,
    onDeleteCategory: suspend (Category) -> Unit,
    onRenameCategory: suspend (Category, String) -> Unit,
    onGetExerciseCount: suspend (Int) -> Int,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var newCategoryName by remember { mutableStateOf("") }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }
    var exerciseCountForDelete by remember { mutableStateOf(0) }
    var editingCategoryId by remember { mutableStateOf<Int?>(null) }
    var editingCategoryName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.manage_categories)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.padding_small)
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        dimensionResource(R.dimen.padding_small)
                    )
                ) {
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        placeholder = { Text(stringResource(R.string.new_category_name)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                onAddCategory(newCategoryName)
                                newCategoryName = ""
                            }
                        },
                        enabled = newCategoryName.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_category)
                        )
                    }
                }
                HorizontalDivider()
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    items(
                        items = availableCategories,
                        key = { it.id }
                    ) { category ->
                        if (editingCategoryId == category.id) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(
                                    dimensionResource(R.dimen.padding_small)
                                )
                            ) {
                                OutlinedTextField(
                                    value = editingCategoryName,
                                    onValueChange = { editingCategoryName = it },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            onRenameCategory(category, editingCategoryName)
                                            editingCategoryId = null
                                        }
                                    },
                                    enabled = editingCategoryName.isNotBlank()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = stringResource(R.string.confirm)
                                    )
                                }
                                IconButton(
                                    onClick = { editingCategoryId = null }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(R.string.cancel)
                                    )
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            editingCategoryId = category.id
                                            editingCategoryName = category.name
                                        }
                                )
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            exerciseCountForDelete =
                                                onGetExerciseCount(category.id)
                                            categoryToDelete = category
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = stringResource(R.string.delete_category)
                                    )
                                }
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.done))
            }
        }
    )

    categoryToDelete?.let { category ->
        DeleteCategoryDialog(
            categoryName = category.name,
            exerciseCount = exerciseCountForDelete,
            onConfirm = {
                coroutineScope.launch {
                    onDeleteCategory(category)
                    categoryToDelete = null
                }
            },
            onDismiss = { categoryToDelete = null }
        )
    }
}

@Composable
private fun DeleteCategoryDialog(
    categoryName: String,
    exerciseCount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_category)) },
        text = {
            Text(
                if (exerciseCount > 0)
                    stringResource(
                        R.string.delete_category_warning,
                        categoryName,
                        exerciseCount
                    )
                else
                    stringResource(R.string.delete_category_confirmation, categoryName)
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.confirm),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
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
                Category(id = 4, name = "Core")
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
            onSaveClick = {},
            onAddCategory = {},
            onDeleteCategory = {},
            onRenameCategory = { _, _ -> },
            onGetExerciseCount = { 0 }
        )
    }
}
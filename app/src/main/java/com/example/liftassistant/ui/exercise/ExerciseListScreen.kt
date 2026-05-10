@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.example.liftassistant.ui.exercise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.liftassistant.LiftAssistantTopAppBar
import com.example.liftassistant.R
import com.example.liftassistant.data.Category
import com.example.liftassistant.data.Exercise
import com.example.liftassistant.data.ExerciseWithCategories
import com.example.liftassistant.ui.AppViewModelProvider
import com.example.liftassistant.ui.navigation.NavigationDestination
import com.example.liftassistant.ui.theme.LiftAssistantTheme

object ExerciseListDestination : NavigationDestination {
    override val route = "exercise_list"
    override val titleRes = R.string.exercise_list_title
}

@Composable
fun ExerciseListScreen(
    navigateToAddExercise: () -> Unit,
    navigateToEditExercise: (Int) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExerciseListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val uiState by viewModel.uiState.collectAsState()
    var expandedExerciseId by rememberSaveable { mutableStateOf<Int?>(null) }
    var selectedCategoryId by rememberSaveable { mutableStateOf<Int?>(null) }
    var exerciseToDelete by remember { mutableStateOf<ExerciseWithCategories?>(null) }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LiftAssistantTopAppBar(
                title = stringResource(ExerciseListDestination.titleRes),
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = onNavigateUp
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = navigateToAddExercise,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_exercise)
                    )
                },
                text = { Text(stringResource(R.string.add_exercise)) }
            )
        }
    ) { innerPadding ->
        ExerciseListBody(
            exerciseList = uiState.exerciseList,
            allCategories = uiState.allCategories,
            expandedExerciseId = expandedExerciseId,
            selectedCategoryId = selectedCategoryId,
            onCategoryFilterClick = { categoryId ->
                selectedCategoryId = if (selectedCategoryId == categoryId) null else categoryId
                expandedExerciseId = null
            },
            onCardClick = { id ->
                expandedExerciseId = if (expandedExerciseId == id) null else id
            },
            onEditClick = navigateToEditExercise,
            onDeleteClick = { exerciseToDelete = it },
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }

    exerciseToDelete?.let { exerciseWithCategories ->
        DeleteConfirmationDialog(
            exerciseName = exerciseWithCategories.exercise.name,
            onConfirm = {
                viewModel.deleteExercise(exerciseWithCategories.exercise)
                exerciseToDelete = null
            },
            onDismiss = { exerciseToDelete = null }
        )
    }
}

@Composable
private fun ExerciseListBody(
    exerciseList: List<ExerciseWithCategories>,
    allCategories: List<Category>,
    expandedExerciseId: Int?,
    selectedCategoryId: Int?,
    onCategoryFilterClick: (Int) -> Unit,
    onCardClick: (Int) -> Unit,
    onEditClick: (Int) -> Unit,
    onDeleteClick: (ExerciseWithCategories) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val filteredExercises = remember(exerciseList, selectedCategoryId) {
        if (selectedCategoryId == null) exerciseList
        else exerciseList.filter { ewc ->
            ewc.categories.any { it.id == selectedCategoryId }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (allCategories.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = dimensionResource(R.dimen.padding_medium)),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
                modifier = Modifier.padding(
                    top = contentPadding.calculateTopPadding(),
                    bottom = dimensionResource(R.dimen.padding_small)
                )
            ) {
                items(items = allCategories, key = { it.id }) { category ->
                    FilterChip(
                        selected = selectedCategoryId == category.id,
                        onClick = { onCategoryFilterClick(category.id) },
                        label = { Text(category.name) }
                    )
                }
            }
        }

        if (filteredExercises.isEmpty()) {
            Text(
                text = stringResource(
                    if (selectedCategoryId != null) R.string.empty_filtered_exercise_list
                    else R.string.empty_exercise_list
                ),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
            )
        } else {
            LazyColumn(
                contentPadding = if (allCategories.isNotEmpty()) {
                    PaddingValues(bottom = contentPadding.calculateBottomPadding())
                } else {
                    contentPadding
                },
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_small))
            ) {
                items(items = filteredExercises, key = { it.exercise.id }) { exerciseWithCategories ->
                    ExerciseCard(
                        exerciseWithCategories = exerciseWithCategories,
                        isExpanded = expandedExerciseId == exerciseWithCategories.exercise.id,
                        onCardClick = { onCardClick(exerciseWithCategories.exercise.id) },
                        onEditClick = { onEditClick(exerciseWithCategories.exercise.id) },
                        onDeleteClick = { onDeleteClick(exerciseWithCategories) },
                        modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                    )
                }
            }
        }
    }
}

@Composable
private fun ExerciseCard(
    exerciseWithCategories: ExerciseWithCategories,
    isExpanded: Boolean,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCardClick() }
                    .padding(dimensionResource(R.dimen.padding_medium)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exerciseWithCategories.exercise.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (exerciseWithCategories.categories.isNotEmpty()) {
                        Text(
                            text = exerciseWithCategories.categories.joinToString(", ") { it.name },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = if (isExpanded)
                        Icons.Default.KeyboardArrowUp
                    else
                        Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    HorizontalDivider()
                    ExerciseDetails(
                        exerciseWithCategories = exerciseWithCategories,
                        onEditClick = onEditClick,
                        onDeleteClick = onDeleteClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ExerciseDetails(
    exerciseWithCategories: ExerciseWithCategories,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val exercise = exerciseWithCategories.exercise
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
    ) {
        ExerciseDetailRow(
            label = stringResource(R.string.pr_weight),
            value = if (exercise.isBodyweight)
                formatBodyweightString(exercise.prWeight)
            else
                stringResource(R.string.weight_value, exercise.prWeight)
        )
        ExerciseDetailRow(
            label = stringResource(R.string.latest_weight),
            value = if (exercise.isBodyweight)
                formatBodyweightString(exercise.latestWeight)
            else
                stringResource(R.string.weight_value, exercise.latestWeight)
        )
        ExerciseDetailRow(
            label = stringResource(R.string.bodyweight_exercise),
            value = if (exercise.isBodyweight) stringResource(R.string.yes)
            else stringResource(R.string.no)
        )

        if (exerciseWithCategories.categories.isNotEmpty()) {
            Text(
                text = stringResource(R.string.categories),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
            ) {
                exerciseWithCategories.categories.forEach { category ->
                    SuggestionChip(
                        onClick = {},
                        label = { Text(category.name) }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_exercise)
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_exercise)
                )
            }
        }
    }
}

@Composable
private fun ExerciseDetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun formatBodyweightString(weight: Float): String {
    return when {
        weight > 0f -> stringResource(R.string.bodyweight_added, weight)
        weight < 0f -> stringResource(R.string.bodyweight_assisted, -weight)
        else -> stringResource(R.string.bodyweight_only)
    }
}

@Composable
private fun DeleteConfirmationDialog(
    exerciseName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_exercise)) },
        text = { Text(stringResource(R.string.delete_exercise_confirmation, exerciseName)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.confirm))
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
private fun ExerciseListEmptyPreview() {
    LiftAssistantTheme {
        ExerciseListBody(
            exerciseList = emptyList(),
            allCategories = emptyList(),
            expandedExerciseId = null,
            selectedCategoryId = null,
            onCategoryFilterClick = {},
            onCardClick = {},
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExerciseListCollapsedPreview() {
    LiftAssistantTheme {
        ExerciseListBody(
            exerciseList = listOf(
                ExerciseWithCategories(
                    exercise = Exercise(id = 1, name = "Bench Press", prWeight = 185f, latestWeight = 175f),
                    categories = listOf(Category(id = 1, name = "Push"), Category(id = 5, name = "Chest"))
                ),
                ExerciseWithCategories(
                    exercise = Exercise(id = 2, name = "Pull-up", isBodyweight = true, prWeight = 45f, latestWeight = 25f),
                    categories = listOf(Category(id = 2, name = "Pull"), Category(id = 6, name = "Biceps"))
                )
            ),
            allCategories = listOf(
                Category(id = 1, name = "Push"),
                Category(id = 2, name = "Pull"),
                Category(id = 3, name = "Legs"),
                Category(id = 4, name = "Core"),
                Category(id = 5, name = "Chest"),
                Category(id = 6, name = "Biceps")
            ),
            expandedExerciseId = null,
            selectedCategoryId = null,
            onCategoryFilterClick = {},
            onCardClick = {},
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExerciseListExpandedPreview() {
    LiftAssistantTheme {
        ExerciseListBody(
            exerciseList = listOf(
                ExerciseWithCategories(
                    exercise = Exercise(id = 1, name = "Bench Press", prWeight = 185f, latestWeight = 175f),
                    categories = listOf(Category(id = 1, name = "Push"), Category(id = 5, name = "Chest"))
                ),
                ExerciseWithCategories(
                    exercise = Exercise(id = 2, name = "Pull-up", isBodyweight = true, prWeight = 45f, latestWeight = 25f),
                    categories = listOf(Category(id = 2, name = "Pull"), Category(id = 6, name = "Biceps"))
                )
            ),
            allCategories = listOf(
                Category(id = 1, name = "Push"),
                Category(id = 2, name = "Pull"),
                Category(id = 3, name = "Legs"),
                Category(id = 4, name = "Core"),
                Category(id = 5, name = "Chest"),
                Category(id = 6, name = "Biceps")
            ),
            expandedExerciseId = 1,
            selectedCategoryId = null,
            onCategoryFilterClick = {},
            onCardClick = {},
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExerciseListFilteredPreview() {
    LiftAssistantTheme {
        ExerciseListBody(
            exerciseList = listOf(
                ExerciseWithCategories(
                    exercise = Exercise(id = 1, name = "Bench Press", prWeight = 185f, latestWeight = 175f),
                    categories = listOf(Category(id = 1, name = "Push"), Category(id = 5, name = "Chest"))
                ),
                ExerciseWithCategories(
                    exercise = Exercise(id = 2, name = "Pull-up", isBodyweight = true, prWeight = 45f, latestWeight = 25f),
                    categories = listOf(Category(id = 2, name = "Pull"), Category(id = 6, name = "Biceps"))
                )
            ),
            allCategories = listOf(
                Category(id = 1, name = "Push"),
                Category(id = 2, name = "Pull"),
                Category(id = 3, name = "Legs"),
                Category(id = 4, name = "Core"),
                Category(id = 5, name = "Chest"),
                Category(id = 6, name = "Biceps")
            ),
            expandedExerciseId = null,
            selectedCategoryId = 1,
            onCategoryFilterClick = {},
            onCardClick = {},
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}
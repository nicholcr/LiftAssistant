@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.liftassistant.ui.exercise

import android.app.AlertDialog
import androidx.compose.runtime.Composable
import com.example.liftassistant.ui.navigation.NavigationDestination
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.liftassistant.LiftAssistantTopAppBar
import com.example.liftassistant.R
import com.example.liftassistant.data.Exercise
import com.example.liftassistant.ui.AppViewModelProvider
import kotlinx.coroutines.launch

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
    val coroutineScope = rememberCoroutineScope()
    var expandedExerciseId by rememberSaveable { mutableStateOf<Int?>(null) }
    var exerciseToDelete by remember { mutableStateOf<Exercise?>(null) }

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
            FloatingActionButton(onClick = navigateToAddExercise) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_exercise)
                )
            }
        }
    ) { innerPadding ->
        ExerciseListBody(
            exerciseList = uiState.exerciseList,
            expandedExerciseId = expandedExerciseId,
            onCardClick = { id ->
                expandedExerciseId = if (expandedExerciseId == id) null else id
            },
            onEditClick = navigateToEditExercise,
            onDeleteClick = { exerciseToDelete = it },
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }

    exerciseToDelete?.let { exercise ->
        DeleteConfirmationDialog(
            exerciseName = exercise.name,
            onConfirm = {
                coroutineScope.launch {
                    viewModel.deleteExercise(exercise)
                }
                exerciseToDelete = null
            },
            onDismiss = { exerciseToDelete = null }
        )
    }
}

@Composable
private fun ExerciseListBody(
    exerciseList: List<Exercise>,
    expandedExerciseId: Int?,
    onCardClick: (Int) -> Unit,
    onEditClick: (Int) -> Unit,
    onDeleteClick: (Exercise) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (exerciseList.isEmpty()) {
            Text(
                text = stringResource(R.string.empty_exercise_list),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding)
            )
        } else {
            LazyColumn(
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_small))
            ) {
                items(items = exerciseList, key = { it.id }) { exercise ->
                    ExerciseCard(
                        exercise = exercise,
                        isExpanded = expandedExerciseId == exercise.id,
                        onCardClick = { onCardClick(exercise.id) },
                        onEditClick = { onEditClick(exercise.id) },
                        onDeleteClick = { onDeleteClick(exercise) },
                        modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                    )
                }
            }
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: Exercise,
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
                        text = exercise.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = exercise.category,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
                        exercise = exercise,
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
    exercise: Exercise,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_small))
    ) {
        ExerciseDetailRow(
            label = stringResource(R.string.pr_weight),
            value = if (exercise.isBodyWeight)
                stringResource(R.string.bodyweight_pr, exercise.prWeight)
            else
                stringResource(R.string.weight_value, exercise.prWeight)
        )
        ExerciseDetailRow(
            label = stringResource(R.string.latest_weight),
            value = if (exercise.isBodyWeight)
                stringResource(R.string.bodyweight_latest, exercise.latestWeight)
            else
                stringResource(R.string.weight_value, exercise.latestWeight)
        )
        ExerciseDetailRow(
            label = stringResource(R.string.bodyweight_exercise),
            value = if (exercise.isBodyWeight)
                stringResource(R.string.yes)
            else
                stringResource(R.string.no)
        )
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
        modifier = Modifier.fillMaxWidth(),
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
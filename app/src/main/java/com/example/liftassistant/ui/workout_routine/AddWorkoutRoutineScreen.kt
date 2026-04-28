@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.example.liftassistant.ui.workout_routine

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.liftassistant.LiftAssistantTopAppBar
import com.example.liftassistant.R
import com.example.liftassistant.data.ExerciseWithCategories
import com.example.liftassistant.ui.AppViewModelProvider
import com.example.liftassistant.ui.navigation.NavigationDestination
import com.example.liftassistant.ui.theme.LiftAssistantTheme
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import com.example.liftassistant.data.Exercise

object AddWorkoutRoutineDestination : NavigationDestination {
    override val route = "add_workout_routine"
    override val titleRes = R.string.add_routine
}

@Composable
fun AddWorkoutRoutineScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddWorkoutRoutineViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val coroutineScope = rememberCoroutineScope()
    val availableExercises by viewModel.availableExercises.collectAsState()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LiftAssistantTopAppBar(
                title = stringResource(AddWorkoutRoutineDestination.titleRes),
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = onNavigateUp
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::addSlot) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_slot)
                )
            }
        }
    ) { innerPadding ->
        RoutineForm(
            formState = viewModel.formState,
            availableExercises = availableExercises,
            onNameChange = viewModel::updateName,
            onSlotUpdate = viewModel::updateSlot,
            onSlotRemove = viewModel::removeSlot,
            onSlotReorder = viewModel::reorderSlots,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveRoutine()
                    navigateBack()
                }
            },
            contentPadding = innerPadding,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun RoutineForm(
    formState: RoutineFormState,
    availableExercises: List<ExerciseWithCategories>,
    onNameChange: (String) -> Unit,
    onSlotUpdate: (Int, RoutineSlotFormState) -> Unit,
    onSlotRemove: (Int) -> Unit,
    onSlotReorder: (Int, Int) -> Unit,
    onSaveClick: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(
        lazyListState = lazyListState,
        onMove = { from, to -> onSlotReorder(from.index - 1, to.index - 1) }
    )

    LazyColumn(
        state = lazyListState,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        modifier = modifier.padding(horizontal = dimensionResource(R.dimen.padding_small))
    ) {
        item {
            Column(
                modifier = Modifier.padding(
                    vertical = dimensionResource(R.dimen.padding_small)
                )
            ) {
                OutlinedTextField(
                    value = formState.name,
                    onValueChange = onNameChange,
                    label = { Text(stringResource(R.string.routine_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = formState.name.isBlank() && formState.name != ""
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
                Text(
                    text = stringResource(R.string.routine_slots_label),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        itemsIndexed(
            items = formState.slots,
            key = { _, slot -> slot.order }
        ) { index, slot ->
            ReorderableItem(
                state = reorderableLazyListState,
                key = slot.order
            ) { isDragging ->
                RoutineSlotCard(
                    slot = slot,
                    index = index,
                    availableExercises = availableExercises,
                    isDragging = isDragging,
                    onSlotUpdate = { updatedSlot -> onSlotUpdate(index, updatedSlot) },
                    onSlotRemove = { onSlotRemove(index) },
                    dragHandle = {
                        DragHandle(
                            modifier = Modifier.draggableHandle()
                        )
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
            Button(
                onClick = onSaveClick,
                enabled = formState.isValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}

@Composable
fun RoutineSlotCard(
    slot: RoutineSlotFormState,
    index: Int,
    availableExercises: List<ExerciseWithCategories>,
    isDragging: Boolean,
    onSlotUpdate: (RoutineSlotFormState) -> Unit,
    onSlotRemove: () -> Unit,
    dragHandle: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    var showExercisePicker by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDragging) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.slot_number, index + 1),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    dragHandle()
                    IconButton(onClick = onSlotRemove) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.remove_slot)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = slot.setScheme,
                onValueChange = { onSlotUpdate(slot.copy(setScheme = it)) },
                label = { Text(stringResource(R.string.set_scheme)) },
                placeholder = { Text(stringResource(R.string.set_scheme_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = slot.setScheme.isBlank() && slot.setScheme != ""
            )

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    onClick = {
                        onSlotUpdate(
                            slot.copy(
                                slotType = SlotType.Fixed,
                                categoryLabel = ""
                            )
                        )
                    },
                    selected = slot.slotType == SlotType.Fixed
                ) {
                    Text(stringResource(R.string.slot_type_fixed))
                }
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    onClick = {
                        onSlotUpdate(
                            slot.copy(
                                slotType = SlotType.Flexible,
                                fixedExercise = null
                            )
                        )
                    },
                    selected = slot.slotType == SlotType.Flexible
                ) {
                    Text(stringResource(R.string.slot_type_flexible))
                }
            }

            when (slot.slotType) {
                SlotType.Fixed -> {
                    OutlinedTextField(
                        value = slot.fixedExercise?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.exercise)) },
                        placeholder = { Text(stringResource(R.string.select_exercise)) },
                        trailingIcon = {
                            IconButton(onClick = { showExercisePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = stringResource(R.string.select_exercise)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isError = slot.fixedExercise == null && slot.setScheme.isNotBlank()
                    )
                }
                SlotType.Flexible -> {
                    OutlinedTextField(
                        value = slot.categoryLabel,
                        onValueChange = { onSlotUpdate(slot.copy(categoryLabel = it)) },
                        label = { Text(stringResource(R.string.category_label)) },
                        placeholder = { Text(stringResource(R.string.category_label_placeholder)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = slot.categoryLabel.isBlank() && slot.categoryLabel != ""
                    )
                }
            }

            OutlinedTextField(
                value = slot.note,
                onValueChange = { onSlotUpdate(slot.copy(note = it)) },
                label = { Text(stringResource(R.string.slot_note)) },
                placeholder = { Text(stringResource(R.string.slot_note_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 1,
                maxLines = 3
            )
        }
    }

    if (showExercisePicker) {
        SlotExercisePickerDialog(
            availableExercises = availableExercises,
            onExerciseSelected = { exerciseWithCategories ->
                onSlotUpdate(slot.copy(fixedExercise = exerciseWithCategories.exercise))
                showExercisePicker = false
            },
            onDismiss = { showExercisePicker = false }
        )
    }
}

@Composable
fun DragHandle(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Default.DragHandle,
        contentDescription = stringResource(R.string.drag_to_reorder),
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

@Composable
private fun SlotExercisePickerDialog(
    availableExercises: List<ExerciseWithCategories>,
    onExerciseSelected: (ExerciseWithCategories) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredExercises = remember(availableExercises, searchQuery) {
        if (searchQuery.isBlank()) availableExercises
        else availableExercises.filter {
            it.exercise.name.contains(searchQuery, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_exercise)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.padding_small)
                )
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(stringResource(R.string.search_exercises)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    items(
                        items = filteredExercises,
                        key = { it.exercise.id }
                    ) { exerciseWithCategories ->
                        TextButton(
                            onClick = { onExerciseSelected(exerciseWithCategories) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = exerciseWithCategories.exercise.name,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                if (exerciseWithCategories.categories.isNotEmpty()) {
                                    Text(
                                        text = exerciseWithCategories.categories
                                            .joinToString(", ") { it.name },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun RoutineFormEmptyPreview() {
    LiftAssistantTheme {
        RoutineForm(
            formState = RoutineFormState(),
            availableExercises = emptyList(),
            onNameChange = {},
            onSlotUpdate = { _, _ -> },
            onSlotRemove = {},
            onSlotReorder = { _, _ -> },
            onSaveClick = {},
            contentPadding = PaddingValues(0.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RoutineFormWithSlotsPreview() {
    LiftAssistantTheme {
        RoutineForm(
            formState = RoutineFormState(
                name = "Upper A",
                slots = listOf(
                    RoutineSlotFormState(
                        order = 0,
                        setScheme = "2x5, 1x5+",
                        slotType = SlotType.Fixed,
                        fixedExercise = Exercise(id = 1, name = "Bench Press"),
                        isValid = true
                    ),
                    RoutineSlotFormState(
                        order = 1,
                        setScheme = "3x8-12",
                        slotType = SlotType.Flexible,
                        categoryLabel = "Push accessory",
                        isValid = true
                    )
                ),
                isValid = true
            ),
            availableExercises = emptyList(),
            onNameChange = {},
            onSlotUpdate = { _, _ -> },
            onSlotRemove = {},
            onSlotReorder = { _, _ -> },
            onSaveClick = {},
            contentPadding = PaddingValues(0.dp)
        )
    }
}
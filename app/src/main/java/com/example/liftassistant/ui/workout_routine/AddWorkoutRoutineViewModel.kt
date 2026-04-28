package com.example.liftassistant.ui.workout_routine

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liftassistant.data.Exercise
import com.example.liftassistant.data.ExerciseWithCategories
import com.example.liftassistant.data.RoutineSlot
import com.example.liftassistant.data.WorkoutRoutine
import com.example.liftassistant.data.repos.ExerciseRepository
import com.example.liftassistant.data.repos.RoutineSlotRepository
import com.example.liftassistant.data.repos.WorkoutRoutineRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SlotType { Fixed, Flexible }

data class RoutineSlotFormState(
    val id: Int? = null,
    val order: Int = 0,
    val setScheme: String = "",
    val slotType: SlotType = SlotType.Fixed,
    val fixedExercise: Exercise? = null,
    val categoryLabel: String = "",
    val note: String = "",
    val isValid: Boolean = false
)

data class RoutineFormState(
    val name: String = "",
    val slots: List<RoutineSlotFormState> = emptyList(),
    val isValid: Boolean = false
)

fun RoutineSlotFormState.toRoutineSlot(routineId: Int): RoutineSlot = RoutineSlot(
    id = id ?: 0,
    routineId = routineId,
    order = order,
    setScheme = setScheme,
    fixedExerciseId = if (slotType == SlotType.Fixed) fixedExercise?.id else null,
    categoryLabel = if (slotType == SlotType.Flexible) categoryLabel else null,
    note = note.ifBlank { null }
)

class AddWorkoutRoutineViewModel(
    private val workoutRoutineRepository: WorkoutRoutineRepository,
    private val routineSlotRepository: RoutineSlotRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    var formState by mutableStateOf(RoutineFormState())
        private set

    val availableExercises: StateFlow<List<ExerciseWithCategories>> =
        exerciseRepository.getAllExercisesWithCategoriesStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = emptyList()
            )

    fun updateName(name: String) {
        formState = formState.copy(
            name = name,
            isValid = validateForm(name, formState.slots)
        )
    }

    fun addSlot() {
        val newSlot = RoutineSlotFormState(order = formState.slots.size)
        val updatedSlots = formState.slots + newSlot
        formState = formState.copy(
            slots = updatedSlots,
            isValid = validateForm(formState.name, updatedSlots)
        )
    }

    fun removeSlot(index: Int) {
        val updatedSlots = formState.slots
            .toMutableList()
            .also { it.removeAt(index) }
            .mapIndexed { i, slot -> slot.copy(order = i) }
        formState = formState.copy(
            slots = updatedSlots,
            isValid = validateForm(formState.name, updatedSlots)
        )
    }

    fun updateSlot(index: Int, updatedSlot: RoutineSlotFormState) {
        val validatedSlot = updatedSlot.copy(isValid = validateSlot(updatedSlot))
        val updatedSlots = formState.slots.toMutableList().also {
            it[index] = validatedSlot
        }
        formState = formState.copy(
            slots = updatedSlots,
            isValid = validateForm(formState.name, updatedSlots)
        )
    }

    fun reorderSlots(fromIndex: Int, toIndex: Int) {
        val updatedSlots = formState.slots.toMutableList().also {
            val slot = it.removeAt(fromIndex)
            it.add(toIndex, slot)
        }.mapIndexed { i, slot -> slot.copy(order = i) }
        formState = formState.copy(slots = updatedSlots)
    }

    private fun validateSlot(slot: RoutineSlotFormState): Boolean {
        if (slot.setScheme.isBlank()) return false
        return when (slot.slotType) {
            SlotType.Fixed -> slot.fixedExercise != null
            SlotType.Flexible -> slot.categoryLabel.isNotBlank()
        }
    }

    private fun validateForm(name: String, slots: List<RoutineSlotFormState>): Boolean {
        if (name.isBlank()) return false
        return slots.all { validateSlot(it) }
    }

    suspend fun saveRoutine() {
        if (!validateForm(formState.name, formState.slots)) return
        val routineId = workoutRoutineRepository.insertWorkoutRoutine(
            WorkoutRoutine(name = formState.name.trim())
        )
        val slots = formState.slots.map { slot ->
            slot.toRoutineSlot(routineId.toInt())
        }
        if (slots.isNotEmpty()) {
            routineSlotRepository.insertAllRoutineSlots(slots)
        }
    }
}
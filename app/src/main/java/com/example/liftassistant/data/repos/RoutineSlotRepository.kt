package com.example.liftassistant.data.repos

import com.example.liftassistant.data.RoutineSlot
import kotlinx.coroutines.flow.Flow

interface RoutineSlotRepository {
    fun getSlotsForRoutineStream(routineId: Int): Flow<List<RoutineSlot>>
    fun getRoutineSlotStream(id: Int): Flow<RoutineSlot?>
    suspend fun insertRoutineSlot(routineSlot: RoutineSlot)
    suspend fun insertAllRoutineSlots(routineSlots: List<RoutineSlot>)
    suspend fun updateRoutineSlot(routineSlot: RoutineSlot)
    suspend fun deleteRoutineSlot(routineSlot: RoutineSlot)
    suspend fun deleteAllSlotsForRoutine(routineId: Int)
    suspend fun getAllSlotsWithCategoryLabel(categoryLabel: String): List<RoutineSlot>
}
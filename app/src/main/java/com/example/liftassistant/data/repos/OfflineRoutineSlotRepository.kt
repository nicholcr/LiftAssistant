package com.example.liftassistant.data.repos

import com.example.liftassistant.data.RoutineSlot
import com.example.liftassistant.data.daos.RoutineSlotDao
import kotlinx.coroutines.flow.Flow

class OfflineRoutineSlotRepository(
    private val routineSlotDao: RoutineSlotDao
) : RoutineSlotRepository {
    override fun getSlotsForRoutineStream(routineId: Int): Flow<List<RoutineSlot>> =
        routineSlotDao.getSlotsForRoutine(routineId)

    override fun getRoutineSlotStream(id: Int): Flow<RoutineSlot?> =
        routineSlotDao.getRoutineSlot(id)

    override suspend fun insertRoutineSlot(routineSlot: RoutineSlot) =
        routineSlotDao.insert(routineSlot)

    override suspend fun insertAllRoutineSlots(routineSlots: List<RoutineSlot>) =
        routineSlotDao.insertAll(routineSlots)

    override suspend fun updateRoutineSlot(routineSlot: RoutineSlot) =
        routineSlotDao.update(routineSlot)

    override suspend fun deleteRoutineSlot(routineSlot: RoutineSlot) =
        routineSlotDao.delete(routineSlot)

    override suspend fun deleteAllSlotsForRoutine(routineId: Int) =
        routineSlotDao.deleteAllSlotsForRoutine(routineId)
}
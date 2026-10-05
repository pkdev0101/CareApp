package com.careapp.app.data.repository

import android.content.Context
import com.careapp.app.data.database.CareAppDatabase
import com.careapp.app.data.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class CareRepository private constructor(context: Context) {
    private val database = CareAppDatabase.getDatabase(context)
    private val medicationDao = database.medicationDao()
    private val routineDao = database.routineDao()
    private val memoryCardDao = database.memoryCardDao()
    private val caregiverNoteDao = database.caregiverNoteDao()

    suspend fun seedIfNeeded() {
        if (medicationDao.countMedications() == 0) SampleData.medications.forEach { medicationDao.insertMedication(it) }
        if (routineDao.countRoutineTasks() == 0) SampleData.routineTasks.forEach { routineDao.insertRoutineTask(it) }
        if (memoryCardDao.countMemoryCards() == 0) SampleData.memoryCards.forEach { memoryCardDao.insertMemoryCard(it) }
        if (caregiverNoteDao.countNotes() == 0) caregiverNoteDao.insertCaregiverNote(
            SampleData.caregiverNote.copy(date = LocalDate.now().toString())
        )
    }

    // ========== MEDICATION OPERATIONS ==========
    fun getAllMedications(): Flow<List<Medication>> = medicationDao.getAllMedications()

    suspend fun getAllMedicationsSync(): List<Medication> = medicationDao.getAllMedicationsSync()

    suspend fun insertMedication(medication: Medication) { medicationDao.insertMedication(medication) }

    suspend fun getMedicationLogsForDate(date: String): List<MedicationLog> = medicationDao.getMedicationLogsByDate(date)

    suspend fun insertMedicationLog(log: MedicationLog) { medicationDao.insertMedicationLog(log) }

    suspend fun checkMedicationAlreadyTaken(medicationId: Int, date: String): MedicationLog? =
        medicationDao.getMedicationLogForDay(medicationId, date)?.takeIf { it.status == "Taken" }

    suspend fun getAllMedicationLogs(): List<MedicationLog> = medicationDao.getAllMedicationLogs()

    // ========== ROUTINE OPERATIONS ==========
    fun getRoutineTasksByCategory(category: String): Flow<List<RoutineTask>> = routineDao.getTasksByCategory(category)

    suspend fun insertRoutineTask(task: RoutineTask) { routineDao.insertRoutineTask(task) }

    suspend fun updateRoutineTask(task: RoutineTask) { routineDao.updateRoutineTask(task) }

    // ========== MEMORY CARD OPERATIONS ==========
    fun getAllMemoryCards(): Flow<List<MemoryCard>> = memoryCardDao.getAllMemoryCards()

    suspend fun insertMemoryCard(card: MemoryCard) { memoryCardDao.insertMemoryCard(card) }

    // ========== CAREGIVER NOTE OPERATIONS ==========
    suspend fun getTodayNote(date: String): CaregiverNote? = caregiverNoteDao.getCaregiverNoteByDate(date)

    suspend fun insertCaregiverNote(note: CaregiverNote) { caregiverNoteDao.insertCaregiverNote(note) }

    suspend fun updateCaregiverNote(note: CaregiverNote) { caregiverNoteDao.updateCaregiverNote(note) }

    suspend fun getLatestCaregiverNote(): CaregiverNote? = caregiverNoteDao.getLatestCaregiverNote()

    companion object {
        @Volatile
        private var instance: CareRepository? = null

        fun getInstance(context: Context): CareRepository =
            instance ?: synchronized(this) {
                CareRepository(context.applicationContext).also { instance = it }
            }
    }
}

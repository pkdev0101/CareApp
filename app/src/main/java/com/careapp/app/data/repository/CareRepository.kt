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
    private val savedPlaceDao = database.savedPlaceDao()

    suspend fun seedIfNeeded() {
        val existingMedicationNames = medicationDao.getAllMedicationsSync().map { it.name }.toSet()
        SampleData.medications.filterNot { it.name in existingMedicationNames }.forEach { medicationDao.insertMedication(it) }
        val existingRoutineTitles = routineDao.getAllRoutineTasks().map { it.title }.toSet()
        SampleData.routineTasks.filterNot { it.title in existingRoutineTitles }.forEach { routineDao.insertRoutineTask(it) }
        val existingMemoryNames = memoryCardDao.getAllMemoryCardsSync().map { it.name }.toSet()
        SampleData.memoryCards.filterNot { it.name in existingMemoryNames }.forEach { memoryCardDao.insertMemoryCard(it) }
        val existingPlaceNames = savedPlaceDao.getAllPlacesSync().map { it.name }.toSet()
        SampleData.savedPlaces.filterNot { it.name in existingPlaceNames }.forEach { savedPlaceDao.insertPlace(it) }
        if (caregiverNoteDao.countNotes() == 0) caregiverNoteDao.insertCaregiverNote(
            SampleData.caregiverNote.copy(date = LocalDate.now().toString())
        )
    }

    // ========== MEDICATION OPERATIONS ==========
    fun getAllMedications(): Flow<List<Medication>> = medicationDao.getAllMedications()

    suspend fun getAllMedicationsSync(): List<Medication> = medicationDao.getAllMedicationsSync()

    suspend fun insertMedication(medication: Medication): Medication = medication.copy(id = medicationDao.insertMedication(medication).toInt())
    suspend fun updateMedication(medication: Medication) { medicationDao.updateMedication(medication) }
    suspend fun deleteMedication(medication: Medication) { medicationDao.deleteMedication(medication) }

    suspend fun getMedicationLogsForDate(date: String): List<MedicationLog> = medicationDao.getMedicationLogsByDate(date)

    suspend fun insertMedicationLog(log: MedicationLog) { medicationDao.insertMedicationLog(log) }

    suspend fun checkMedicationAlreadyTaken(medicationId: Int, date: String): MedicationLog? =
        medicationDao.getMedicationLogForDay(medicationId, date)?.takeIf { it.status == "Taken" }

    suspend fun getAllMedicationLogs(): List<MedicationLog> = medicationDao.getAllMedicationLogs()

    // ========== ROUTINE OPERATIONS ==========
    fun getRoutineTasksByCategory(category: String): Flow<List<RoutineTask>> = routineDao.getTasksByCategory(category)

    suspend fun insertRoutineTask(task: RoutineTask) { routineDao.insertRoutineTask(task) }
    suspend fun getAllRoutineTasksSync(): List<RoutineTask> = routineDao.getAllRoutineTasks()

    suspend fun updateRoutineTask(task: RoutineTask) { routineDao.updateRoutineTask(task) }
    suspend fun deleteRoutineTask(task: RoutineTask) { routineDao.deleteRoutineTask(task) }

    // ========== MEMORY CARD OPERATIONS ==========
    fun getAllMemoryCards(): Flow<List<MemoryCard>> = memoryCardDao.getAllMemoryCards()

    suspend fun insertMemoryCard(card: MemoryCard) { memoryCardDao.insertMemoryCard(card) }
    suspend fun updateMemoryCard(card: MemoryCard) { memoryCardDao.updateMemoryCard(card) }
    suspend fun deleteMemoryCard(card: MemoryCard) { memoryCardDao.deleteMemoryCard(card) }

    // ========== CAREGIVER NOTE OPERATIONS ==========
    suspend fun getTodayNote(date: String): CaregiverNote? = caregiverNoteDao.getCaregiverNoteByDate(date)

    suspend fun insertCaregiverNote(note: CaregiverNote) { caregiverNoteDao.insertCaregiverNote(note) }

    suspend fun updateCaregiverNote(note: CaregiverNote) { caregiverNoteDao.updateCaregiverNote(note) }

    suspend fun getAllCaregiverNotes(): List<CaregiverNote> = caregiverNoteDao.getAllCaregiverNotes()

    fun getAllPlaces(): Flow<List<SavedPlace>> = savedPlaceDao.getAllPlaces()
    suspend fun insertPlace(place: SavedPlace) { savedPlaceDao.insertPlace(place) }
    suspend fun updatePlace(place: SavedPlace) { savedPlaceDao.updatePlace(place) }
    suspend fun deletePlace(place: SavedPlace) { savedPlaceDao.deletePlace(place) }

    companion object {
        @Volatile
        private var instance: CareRepository? = null

        fun getInstance(context: Context): CareRepository =
            instance ?: synchronized(this) {
                CareRepository(context.applicationContext).also { instance = it }
            }
    }
}

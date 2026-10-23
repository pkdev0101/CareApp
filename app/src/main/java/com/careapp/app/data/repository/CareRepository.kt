package com.careapp.app.data.repository

import android.content.Context
import com.careapp.app.data.database.CareAppDatabase
import com.careapp.app.data.*
import com.careapp.app.util.DemoModeController
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class CareRepository private constructor(context: Context, databaseName: String) {
    private val database = CareAppDatabase.getDatabase(context, databaseName)
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

    /** Seeds only the separate demo database. It never touches the real database. */
    suspend fun seedDemoIfNeeded() {
        val existingMedicationNames = medicationDao.getAllMedicationsSync().map { it.name }.toSet()
        listOf(
            Medication(name = "Demo Morning Medication", dose = "1 demo tablet", scheduledTime = "08:00", instructions = "Fictional presentation record only", pillDescription = "Demo label", reason = "Demo data only", doctor = "Demo profile"),
            Medication(name = "Demo Evening Medication", dose = "1 demo tablet", scheduledTime = "18:00", instructions = "Fictional presentation record only", pillDescription = "Demo label", reason = "Demo data only", doctor = "Demo profile")
        ).filterNot { it.name in existingMedicationNames }.forEach { medicationDao.insertMedication(it) }

        val existingRoutineKeys = routineDao.getAllRoutineTasks().map { "${it.category}|${it.title}" }.toSet()
        listOf(
            RoutineTask(title = "Brush teeth", category = "Morning", suggestedTime = "8:30 AM"),
            RoutineTask(title = "Get dressed", category = "Morning", suggestedTime = "8:45 AM"),
            RoutineTask(title = "Put on shoes", category = "Morning", suggestedTime = "9:00 AM")
        ).filterNot { "${it.category}|${it.title}" in existingRoutineKeys }.forEach { routineDao.insertRoutineTask(it) }
        if (memoryCardDao.getAllMemoryCardsSync().isEmpty()) {
            memoryCardDao.insertMemoryCard(MemoryCard(name = "Sarah Miller", relationship = "Daughter", memoryCue = "Fictional demo caregiver", helpsWith = "Demo care support", phone = "555-010-0199"))
        }
        if (savedPlaceDao.getAllPlacesSync().isEmpty()) {
            savedPlaceDao.insertPlace(SavedPlace(name = "Demo Home", type = "Demo safe place", latitude = 0.0, longitude = 0.0, notes = "Fictional location — not real GPS"))
        }
        if (caregiverNoteDao.countNotes() == 0) caregiverNoteDao.insertCaregiverNote(CaregiverNote(date = LocalDate.now().toString(), nextSteps = "Family Visit at 2:00 PM — Sarah is visiting. Demo data only."))
    }

    // ========== MEDICATION OPERATIONS ==========
    fun getAllMedications(): Flow<List<Medication>> = medicationDao.getAllMedications()

    suspend fun getAllMedicationsSync(): List<Medication> = medicationDao.getAllMedicationsSync()

    suspend fun getAllMemoryCardsSync(): List<MemoryCard> = memoryCardDao.getAllMemoryCardsSync()

    suspend fun getAllPlacesSync(): List<SavedPlace> = savedPlaceDao.getAllPlacesSync()

    suspend fun hasAnyUserData(): Boolean = getAllMedicationsSync().isNotEmpty() ||
        getAllRoutineTasksSync().isNotEmpty() ||
        getAllMemoryCardsSync().isNotEmpty() ||
        getAllPlacesSync().isNotEmpty()

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

    fun getAllRoutineTasks(): Flow<List<RoutineTask>> = routineDao.getAllRoutineTasksFlow()

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
        private var realInstance: CareRepository? = null
        private var demoInstance: CareRepository? = null

        fun getInstance(context: Context): CareRepository {
            if (DemoModeController.isActive(context)) {
                return synchronized(this) {
                    demoInstance ?: CareRepository(context.applicationContext, "careanchor_demo_database").also { demoInstance = it }
                }
            }
            return synchronized(this) {
                realInstance ?: CareRepository(context.applicationContext, "careanchor_database").also { realInstance = it }
            }
        }

        fun getRealInstance(context: Context): CareRepository = synchronized(this) {
            realInstance ?: CareRepository(context.applicationContext, "careanchor_database").also { realInstance = it }
        }
    }
}

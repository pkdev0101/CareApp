package com.careapp.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.careapp.app.data.Medication
import com.careapp.app.data.MedicationLog
import com.careapp.app.data.repository.CareRepository
import com.careapp.app.util.MedicationManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class MedicationViewModel(private val repository: CareRepository) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val today = LocalDate.now().format(dateFormatter)

    // Medications list
    val medications: StateFlow<List<Medication>> = repository.getAllMedications()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Medication logs for today
    private val _todayLogs = MutableStateFlow<List<MedicationLog>>(emptyList())
    val todayLogs: StateFlow<List<MedicationLog>> = _todayLogs.asStateFlow()

    val overdueMedications: StateFlow<List<Medication>> = combine(medications, todayLogs) { medicationList, logs ->
        val now = LocalTime.now()
        medicationList.filter { medication ->
            val scheduled = parseScheduledTime(medication.scheduledTime) ?: return@filter false
            val resolved = logs.any {
                it.medicationId == medication.id && it.status in setOf("Taken", "Skipped", "Needs Help", "Need Help")
            }
            !resolved && now.isAfter(scheduled.plusMinutes(30))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Dialog states
    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _dialogMessage = MutableStateFlow("")
    val dialogMessage: StateFlow<String> = _dialogMessage.asStateFlow()

    private val _dialogTitle = MutableStateFlow("")
    val dialogTitle: StateFlow<String> = _dialogTitle.asStateFlow()

    private val _showDuplicateWarning = MutableStateFlow(false)
    val showDuplicateWarning: StateFlow<Boolean> = _showDuplicateWarning.asStateFlow()

    private val _duplicateWarningTime = MutableStateFlow("")
    val duplicateWarningTime: StateFlow<String> = _duplicateWarningTime.asStateFlow()

    init {
        // Load today's medication logs
        viewModelScope.launch {
            _todayLogs.value = repository.getMedicationLogsForDate(today)
        }
    }

    // ========== TAKE MEDICATION ==========
    fun takeMedication(medicationId: Int, medication: Medication) {
        viewModelScope.launch {
            val existingLog = repository.checkMedicationAlreadyTaken(medicationId, today)

            if (existingLog != null) {
                // Already taken - show warning
                _showDuplicateWarning.value = true
                _duplicateWarningTime.value = existingLog.completedTime ?: "unknown time"
            } else {
                // Not taken yet - proceed
                val currentTime = LocalTime.now().format(timeFormatter)
                val log = MedicationLog(
                    medicationId = medicationId,
                    date = today,
                    scheduledTime = medication.scheduledTime,
                    status = "Taken",
                    completedTime = currentTime,
                    notes = "Marked taken by senior"
                )
                repository.insertMedicationLog(log)

                _dialogTitle.value = "✓ Medication Recorded"
                _dialogMessage.value = "You took ${medication.name} at $currentTime.\n\nIf this was a mistake, ask your caregiver."
                _showDialog.value = true

                // Reload today's logs
                _todayLogs.value = repository.getMedicationLogsForDate(today)
            }
        }
    }

    // ========== REMIND LATER ==========
    fun remindLater(medicationId: Int, medication: Medication) {
        viewModelScope.launch {
            val currentTime = LocalTime.now().format(timeFormatter)
            val log = MedicationLog(
                medicationId = medicationId,
                date = today,
                scheduledTime = medication.scheduledTime,
                status = "Remind Later",
                completedTime = currentTime,
                notes = "Reminder set by senior"
            )
            repository.insertMedicationLog(log)

            _dialogTitle.value = "Reminder Set"
            _dialogMessage.value = "We'll remind you about ${medication.name} again soon.\n\nTime: ${medication.scheduledTime}"
            _showDialog.value = true
        }
    }

    // ========== NEED HELP ==========
    fun needHelp(medicationId: Int, medication: Medication) {
        viewModelScope.launch {
            val currentTime = LocalTime.now().format(timeFormatter)
            val log = MedicationLog(
                medicationId = medicationId,
                date = today,
                scheduledTime = medication.scheduledTime,
                status = "Needs Help",
                completedTime = currentTime,
                notes = "Help requested by senior"
            )
            repository.insertMedicationLog(log)

            _dialogTitle.value = "Help Requested"
            _dialogMessage.value = "A caregiver has been alerted.\n\n${medication.name} - ${medication.dose}\n\nInstructions: ${medication.instructions}"
            _showDialog.value = true
        }
    }

    // ========== DISMISS DIALOGS ==========
    fun dismissDialog() {
        _showDialog.value = false
        _dialogTitle.value = ""
        _dialogMessage.value = ""
    }

    fun dismissDuplicateWarning() {
        _showDuplicateWarning.value = false
        _duplicateWarningTime.value = ""
    }

    // ========== GET MEDICATION STATUS ==========
    fun getMedicationStatus(medicationId: Int): String {
        val logs = todayLogs.value.filter { it.medicationId == medicationId }
        return when {
            logs.isEmpty() -> "Pending"
            logs.any { it.status == "Taken" } -> "Taken"
            logs.any { it.status == "Needs Help" } -> "Needs Help"
            else -> "Pending"
        }
    }

    fun getMedicationCompletionTime(medicationId: Int): String? {
        return todayLogs.value
            .find { it.medicationId == medicationId && it.status == "Taken" }
            ?.completedTime
    }

    private fun parseScheduledTime(value: String): LocalTime? = try {
        LocalTime.parse(value.uppercase(), DateTimeFormatter.ofPattern("h:mm a"))
    } catch (_: DateTimeParseException) {
        null
    }
}

package com.careapp.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.careapp.app.data.Medication
import com.careapp.app.data.MedicationLog
import com.careapp.app.data.repository.CareRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MedicationHistoryViewModel(private val repository: CareRepository) : ViewModel() {
    data class HistoryEntry(
        val date: LocalDate,
        val medication: Medication,
        val scheduledTime: String,
        val status: String,
        val completedTime: String?,
        val notes: String
    )

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val _historyEntries = MutableStateFlow<Map<LocalDate, List<HistoryEntry>>>(emptyMap())
    val historyEntries: StateFlow<Map<LocalDate, List<HistoryEntry>>> = _historyEntries.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            val medications = repository.getAllMedicationsSync()
            val logs = repository.getAllMedicationLogs()
            val today = LocalDate.now()
            _historyEntries.value = (0L..6L).associate { offset ->
                val date = today.minusDays(offset)
                val dateText = date.format(dateFormatter)
                date to medications.map { medication ->
                    val log = logs.lastOrNull { it.medicationId == medication.id && it.date == dateText }
                    HistoryEntry(
                        date = date,
                        medication = medication,
                        scheduledTime = log?.scheduledTime ?: medication.scheduledTime,
                        status = resolveStatus(date, medication.scheduledTime, log),
                        completedTime = log?.completedTime,
                        notes = log?.notes.orEmpty()
                    )
                }.sortedBy { it.scheduledTime }
            }
        }
    }

    private fun resolveStatus(date: LocalDate, scheduled: String, log: MedicationLog?): String {
        when (log?.status) {
            "Taken" -> return "Taken"
            "Skipped" -> return "Skipped"
            "Needs Help", "Need Help" -> return "Need Help"
        }
        if (date.isBefore(LocalDate.now())) return "Missed"
        return try {
            val time = LocalTime.parse(scheduled.uppercase(), DateTimeFormatter.ofPattern("h:mm a"))
            if (LocalDateTime.now().isAfter(LocalDate.now().atTime(time).plusMinutes(30))) "Missed" else "Pending"
        } catch (_: Exception) {
            "Pending"
        }
    }

    fun getStatusIcon(status: String): String = when (status) {
        "Taken" -> "✓"
        "Missed" -> "!"
        "Need Help" -> "?"
        else -> "○"
    }
}

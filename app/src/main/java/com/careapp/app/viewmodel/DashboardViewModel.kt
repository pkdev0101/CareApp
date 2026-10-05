package com.careapp.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.careapp.app.data.Medication
import com.careapp.app.data.repository.CareRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DashboardViewModel(private val repository: CareRepository) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val today = LocalDate.now().format(dateFormatter)

    // Medications
    private val _medicationCount = MutableStateFlow(0)
    val medicationCount: StateFlow<Int> = _medicationCount.asStateFlow()

    private val _medicationsTaken = MutableStateFlow(0)
    val medicationsTaken: StateFlow<Int> = _medicationsTaken.asStateFlow()

    private val _medicationsPending = MutableStateFlow(0)
    val medicationsPending: StateFlow<Int> = _medicationsPending.asStateFlow()

    // Routine tasks
    private val _routineTaskCount = MutableStateFlow(0)
    val routineTaskCount: StateFlow<Int> = _routineTaskCount.asStateFlow()

    private val _routineTasksCompleted = MutableStateFlow(0)
    val routineTasksCompleted: StateFlow<Int> = _routineTasksCompleted.asStateFlow()

    // Adherence percentage
    private val _adherencePercentage = MutableStateFlow(0)
    val adherencePercentage: StateFlow<Int> = _adherencePercentage.asStateFlow()

    // Next medication
    private val _nextMedication = MutableStateFlow<Medication?>(null)
    val nextMedication: StateFlow<Medication?> = _nextMedication.asStateFlow()

    // Status messages
    private val _statusMessage = MutableStateFlow("💊 Medications: 0 due")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        // Load medications reactively
        viewModelScope.launch {
            repository.getAllMedications().collect { medications ->
                _medicationCount.value = medications.size

                // Calculate adherence
                try {
                    val todayLogs = repository.getMedicationLogsForDate(today)
                    val takenCount = todayLogs.count { it.status == "Taken" }
                    _medicationsTaken.value = takenCount
                    _medicationsPending.value = medications.size - takenCount

                    if (medications.isNotEmpty()) {
                        _adherencePercentage.value = (takenCount * 100) / medications.size
                    }

                    // Find next medication
                    val currentTime = LocalTime.now()
                    val nextMed = medications.filter { med ->
                        try {
                            val medTime = LocalTime.parse(med.scheduledTime, DateTimeFormatter.ofPattern("HH:mm"))
                            medTime.isAfter(currentTime)
                        } catch (e: Exception) {
                            false
                        }
                    }.minByOrNull { med ->
                        try {
                            LocalTime.parse(med.scheduledTime, DateTimeFormatter.ofPattern("HH:mm"))
                        } catch (e: Exception) {
                            LocalTime.MIDNIGHT
                        }
                    }
                    _nextMedication.value = nextMed

                    updateStatusMessage()
                } catch (e: Exception) {
                    _statusMessage.value = "Error loading data"
                }
            }
        }

        // Load routine tasks reactively
        viewModelScope.launch {
            repository.getRoutineTasksByCategory("Morning").collect { tasks ->
                _routineTaskCount.value = tasks.size
                _routineTasksCompleted.value = tasks.count { it.completed }
                updateStatusMessage()
            }
        }
    }

    private fun updateStatusMessage() {
        val message = buildString {
            append("💊 Medications: ${_medicationCount.value} due")
            if (_medicationsTaken.value > 0) {
                append(" | ${_medicationsTaken.value} taken")
            }
            if (_medicationsPending.value > 0) {
                append(" | ${_medicationsPending.value} pending")
            }
        }
        _statusMessage.value = message
    }
}

package com.careapp.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.careapp.app.data.MedicationLog
import com.careapp.app.data.repository.CareRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MedicationHistoryViewModel(private val repository: CareRepository) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val displayDateFormatter = DateTimeFormatter.ofPattern("MMM dd")

    // Selected date for history view
    private val _selectedDate = MutableStateFlow(LocalDate.now().format(dateFormatter))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // History entries
    private val _historyEntries = MutableStateFlow<List<MedicationLog>>(emptyList())
    val historyEntries: StateFlow<List<MedicationLog>> = _historyEntries.asStateFlow()

    // Display date
    private val _displayDate = MutableStateFlow("")
    val displayDate: StateFlow<String> = _displayDate.asStateFlow()

    // Completion count
    private val _completionCount = MutableStateFlow(0)
    val completionCount: StateFlow<Int> = _completionCount.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _selectedDate.collect { date ->
                try {
                    val logs = repository.getMedicationLogsForDate(date)
                    _historyEntries.value = logs.sortedBy { it.completedTime ?: "23:59" }
                    _completionCount.value = logs.count { it.status == "Taken" }
                    _displayDate.value = try {
                        val parsedDate = LocalDate.parse(date, dateFormatter)
                        parsedDate.format(displayDateFormatter)
                    } catch (e: Exception) {
                        date
                    }
                } catch (e: Exception) {
                    _historyEntries.value = emptyList()
                }
            }
        }
    }

    fun previousDay() {
        try {
            val current = LocalDate.parse(_selectedDate.value, dateFormatter)
            val previous = current.minusDays(1)
            _selectedDate.value = previous.format(dateFormatter)
        } catch (e: Exception) {
            // Stay on current date if parsing fails
        }
    }

    fun nextDay() {
        try {
            val current = LocalDate.parse(_selectedDate.value, dateFormatter)
            val next = current.plusDays(1)
            if (!next.isAfter(LocalDate.now())) {
                _selectedDate.value = next.format(dateFormatter)
            }
        } catch (e: Exception) {
            // Stay on current date if parsing fails
        }
    }

    fun getStatusIcon(status: String?): String = when (status) {
        "Taken" -> "✓"
        "Skipped" -> "◯"
        "Missed" -> "✗"
        "Needs Help" -> "⚠️"
        else -> "○"
    }
}

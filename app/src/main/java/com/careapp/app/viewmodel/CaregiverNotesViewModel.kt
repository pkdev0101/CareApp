package com.careapp.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.careapp.app.data.CaregiverNote
import com.careapp.app.data.repository.CareRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CaregiverNotesViewModel(private val repository: CareRepository) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val today = LocalDate.now().format(dateFormatter)

    // Current note content
    private val _noteContent = MutableStateFlow("")
    val noteContent: StateFlow<String> = _noteContent.asStateFlow()

    // Meal tracking
    private val _breakfastCompleted = MutableStateFlow(false)
    val breakfastCompleted: StateFlow<Boolean> = _breakfastCompleted.asStateFlow()

    private val _lunchCompleted = MutableStateFlow(false)
    val lunchCompleted: StateFlow<Boolean> = _lunchCompleted.asStateFlow()

    private val _dinnerCompleted = MutableStateFlow(false)
    val dinnerCompleted: StateFlow<Boolean> = _dinnerCompleted.asStateFlow()

    // Mood tracking
    private val _mood = MutableStateFlow("Happy")
    val mood: StateFlow<String> = _mood.asStateFlow()

    // Sleep quality
    private val _sleepQuality = MutableStateFlow("Good")
    val sleepQuality: StateFlow<String> = _sleepQuality.asStateFlow()

    // Dialog states
    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _dialogMessage = MutableStateFlow("")
    val dialogMessage: StateFlow<String> = _dialogMessage.asStateFlow()

    private val _generatedSummary = MutableStateFlow("")
    val generatedSummary: StateFlow<String> = _generatedSummary.asStateFlow()

    // Generated summary text for display
    private val _summaryText = MutableStateFlow("")
    val summaryText: StateFlow<String> = _summaryText.asStateFlow()

    init {
        loadTodayNote()
    }

    // ========== LOAD TODAY'S NOTE ==========
    private fun loadTodayNote() {
        viewModelScope.launch {
            val existingNote = repository.getTodayNote(today)
            if (existingNote != null) {
                _noteContent.value = existingNote.mealNotes
                _mood.value = existingNote.mood
                _sleepQuality.value = existingNote.sleepQuality
            }
        }
    }

    // ========== UPDATE NOTE CONTENT ==========
    fun updateNoteContent(newContent: String) {
        _noteContent.value = newContent
    }

    // ========== TOGGLE MEALS ==========
    fun toggleBreakfast() {
        _breakfastCompleted.value = !_breakfastCompleted.value
    }

    fun toggleLunch() {
        _lunchCompleted.value = !_lunchCompleted.value
    }

    fun toggleDinner() {
        _dinnerCompleted.value = !_dinnerCompleted.value
    }

    // ========== UPDATE MOOD ==========
    fun setMood(newMood: String) {
        _mood.value = newMood
    }

    // ========== UPDATE SLEEP QUALITY ==========
    fun setSleepQuality(quality: String) {
        _sleepQuality.value = quality
    }

    // ========== SAVE NOTE ==========
    fun saveNote() {
        viewModelScope.launch {
            val currentTime = LocalDateTime.now().format(timeFormatter)

            val mealSummary = buildString {
                if (_breakfastCompleted.value) append("Breakfast completed. ")
                if (_lunchCompleted.value) append("Lunch completed. ")
                if (_dinnerCompleted.value) append("Dinner completed. ")
            }

            val note = CaregiverNote(
                date = today,
                mealNotes = mealSummary + _noteContent.value,
                mood = _mood.value,
                sleepQuality = _sleepQuality.value
            )

            val existingNote = repository.getTodayNote(today)
            if (existingNote != null) {
                repository.updateCaregiverNote(note.copy(id = existingNote.id))
            } else {
                repository.insertCaregiverNote(note)
            }

            _dialogMessage.value = "Handoff note saved successfully at $currentTime."
            _showDialog.value = true
        }
    }

    // ========== GENERATE HANDOFF SUMMARY ==========
    fun generateHandoffSummary() {
        val mealsCompleted = listOfNotNull(
            if (_breakfastCompleted.value) "breakfast" else null,
            if (_lunchCompleted.value) "lunch" else null,
            if (_dinnerCompleted.value) "dinner" else null
        )

        val mealText = when {
            mealsCompleted.isEmpty() -> "No meals completed"
            mealsCompleted.size == 3 -> "All meals completed"
            else -> mealsCompleted.joinToString(" and ") + " completed"
        }

        val summary = buildString {
            append("Handoff Summary:\n\n")
            append("Meals: $mealText\n")
            append("Mood: ${_mood.value}\n")
            append("Sleep Quality: ${_sleepQuality.value}\n")
            append("Notes: ${_noteContent.value.ifEmpty { "No additional notes" }}\n\n")
            append("Next caregiver should monitor for any changes in mood or behavior.")
        }

        _summaryText.value = summary
        _generatedSummary.value = summary
    }

    // ========== DISMISS DIALOG ==========
    fun dismissDialog() {
        _showDialog.value = false
        _dialogMessage.value = ""
    }
}

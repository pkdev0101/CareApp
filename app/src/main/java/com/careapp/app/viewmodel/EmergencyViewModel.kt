package com.careapp.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.careapp.app.data.repository.CareRepository
import com.careapp.app.data.EmergencyTimelineEntry
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EmergencyViewModel(private val repository: CareRepository) : ViewModel() {

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    // Emergency card display
    private val _showEmergencyCard = MutableStateFlow(false)
    val showEmergencyCard: StateFlow<Boolean> = _showEmergencyCard.asStateFlow()

    // Dialog states
    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _dialogMessage = MutableStateFlow("")
    val dialogMessage: StateFlow<String> = _dialogMessage.asStateFlow()

    private val _dialogTitle = MutableStateFlow("")
    val dialogTitle: StateFlow<String> = _dialogTitle.asStateFlow()

    // Emergency timeline
    private val _emergencyTimeline = MutableStateFlow<List<EmergencyTimelineEntry>>(emptyList())
    val emergencyTimeline: StateFlow<List<EmergencyTimelineEntry>> = _emergencyTimeline.asStateFlow()

    // Countdown state for "I Fell" feature
    private val _countdownValue = MutableStateFlow(15)
    val countdownValue: StateFlow<Int> = _countdownValue.asStateFlow()

    private val _showCountdown = MutableStateFlow(false)
    val showCountdown: StateFlow<Boolean> = _showCountdown.asStateFlow()

    // Emergency card content (from EmergencyManager)
    private val _emergencyCardContent = MutableStateFlow("")
    val emergencyCardContent: StateFlow<String> = _emergencyCardContent.asStateFlow()

    init {
        loadEmergencyCard()
        viewModelScope.launch {
            addTimelineEntry("Emergency Mode opened")
        }
    }

    // ========== LOAD EMERGENCY CARD ==========
    private fun loadEmergencyCard() {
        // Demo emergency card content
        val card = """
            EMERGENCY INFORMATION

            Name: Robert Wilson
            Age: 72

            MEMORY STATUS:
            I may have memory loss. Please speak slowly and tell me where I am.

            PRIMARY CAREGIVER:
            Maya Wilson
            Phone: 555-123-4567

            ALLERGIES:
            Penicillin, Shellfish

            CURRENT MEDICATIONS:
            - Metformin 500mg (Diabetes)
            - Aspirin 81mg (Heart health)
            - Lisinopril 10mg (Blood pressure)

            MEDICAL CONDITIONS:
            - Type 2 Diabetes
            - High Blood Pressure
            - Memory Loss/Dementia

            CARE INSTRUCTIONS:
            Be patient. Speak clearly. Call Maya first before calling 911.
        """.trimIndent()
        _emergencyCardContent.value = card
    }

    // ========== CALL CAREGIVER ==========
    fun callCaregiver() {
        viewModelScope.launch {
            val timestamp = LocalDateTime.now().format(timeFormatter)
            addTimelineEntry("Called primary caregiver at $timestamp")

            _dialogTitle.value = "Calling Caregiver"
            _dialogMessage.value = "Dialing Maya Wilson at 555-123-4567...\n\n" +
                "Phone dialer should open on your device."
            _showDialog.value = true
        }
    }

    // ========== CALL 911 ==========
    fun call911() {
        viewModelScope.launch {
            val timestamp = LocalDateTime.now().format(timeFormatter)
            addTimelineEntry("911 button tapped at $timestamp")

            _dialogTitle.value = "Emergency Services"
            _dialogMessage.value = "This is a demo app. In a real emergency, please call 911 directly.\n\n" +
                "Your location, name, and medical information can be shared with emergency responders."
            _showDialog.value = true
        }
    }

    // ========== SHOW EMERGENCY CARD ==========
    fun showEmergencyCard() {
        viewModelScope.launch {
            val timestamp = LocalDateTime.now().format(timeFormatter)
            addTimelineEntry("Emergency Card viewed at $timestamp")
            _showEmergencyCard.value = true
        }
    }

    fun locationViewed() {
        addTimelineEntry("Location viewed", "The user opened the lost-mode location screen.", "Info")
    }

    fun hideEmergencyCard() {
        _showEmergencyCard.value = false
    }

    // ========== I FELL / NEED HELP ==========
    fun needImmediateHelp() {
        val timestamp = LocalDateTime.now().format(timeFormatter)
        viewModelScope.launch {
            addTimelineEntry("Help requested at $timestamp")
        }

        _countdownValue.value = 15
        _showCountdown.value = true
    }

    // ========== COUNTDOWN FOR CANCELLATION ==========
    fun decrementCountdown() {
        if (_countdownValue.value > 0) {
            _countdownValue.value--
        }

        if (_countdownValue.value == 0) {
            _showCountdown.value = false
            viewModelScope.launch {
                alertCaregiver()
            }
        }
    }

    fun cancelHelp() {
        addTimelineEntry("Alert cancelled", "The help countdown was cancelled by the user.", "Info")
        _showCountdown.value = false
        _countdownValue.value = 15

        _dialogTitle.value = "Cancelled"
        _dialogMessage.value = "Help request cancelled."
        _showDialog.value = true
    }

    // ========== ALERT CAREGIVER ==========
    private suspend fun alertCaregiver() {
        val timestamp = LocalDateTime.now().format(timeFormatter)
        addTimelineEntry("Emergency alert sent to caregiver at $timestamp")

        _dialogTitle.value = "⚠️ Help Requested"
        _dialogMessage.value = "An alert has been sent to Maya Wilson.\n\n" +
            "Time: $timestamp\n" +
            "Location sharing is enabled.\n\n" +
            "Stay calm. Help is on the way."
        _showDialog.value = true
    }

    // ========== MARK AS SAFE ==========
    fun markAsSafe() {
        viewModelScope.launch {
            val timestamp = LocalDateTime.now().format(timeFormatter)
            addTimelineEntry("Marked as safe at $timestamp")

            _dialogTitle.value = "✓ Status Updated"
            _dialogMessage.value = "You have been marked as safe.\n\n" +
                "Your caregiver has been notified that you do not need help."
            _showDialog.value = true
        }
    }

    // ========== EMERGENCY TIMELINE ==========
    private fun addTimelineEntry(title: String, description: String = "Emergency action recorded.", severity: String = "Info") {
        val timestamp = LocalDateTime.now().format(timeFormatter)
        val entry = EmergencyTimelineEntry(System.currentTimeMillis(), timestamp, title, description, severity)
        _emergencyTimeline.value = listOf(entry) + _emergencyTimeline.value
    }

    fun getTimelineFormatted(): String {
        return _emergencyTimeline.value.joinToString("\n") { "${it.timestamp} — ${it.title}: ${it.description}" }
    }

    // ========== DISMISS DIALOG ==========
    fun dismissDialog() {
        _showDialog.value = false
        _dialogTitle.value = ""
        _dialogMessage.value = ""
    }
}

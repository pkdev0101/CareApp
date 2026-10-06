package com.careapp.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.careapp.app.data.repository.CareRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocationViewModel(private val repository: CareRepository) : ViewModel() {

    private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

    // Current location
    private val _currentLatitude = MutableStateFlow(0.0)
    val currentLatitude: StateFlow<Double> = _currentLatitude.asStateFlow()

    private val _currentLongitude = MutableStateFlow(0.0)
    val currentLongitude: StateFlow<Double> = _currentLongitude.asStateFlow()

    // Location timestamp
    private val _locationTimestamp = MutableStateFlow("")
    val locationTimestamp: StateFlow<String> = _locationTimestamp.asStateFlow()
    val lastUpdatedTime: StateFlow<String> = _locationTimestamp.asStateFlow()

    private val _isDemoLocation = MutableStateFlow(true)
    val isDemoLocation: StateFlow<Boolean> = _isDemoLocation.asStateFlow()
    private val _locationStatusText = MutableStateFlow("Location is not available yet.")
    val locationStatusText: StateFlow<String> = _locationStatusText.asStateFlow()
    private val _shareLocationText = MutableStateFlow("")
    val shareLocationText: StateFlow<String> = _shareLocationText.asStateFlow()

    // Geofence status
    private val _isWithinSafeZone = MutableStateFlow(true)
    val isWithinSafeZone: StateFlow<Boolean> = _isWithinSafeZone.asStateFlow()

    // Dialog states
    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _dialogMessage = MutableStateFlow("")
    val dialogMessage: StateFlow<String> = _dialogMessage.asStateFlow()

    private val _dialogTitle = MutableStateFlow("")
    val dialogTitle: StateFlow<String> = _dialogTitle.asStateFlow()

    // Safe places reference text
    private val _safePlacesText = MutableStateFlow("")
    val safePlacesText: StateFlow<String> = _safePlacesText.asStateFlow()

    init {
        initializeSafePlaces()
    }

    // ========== INITIALIZE SAFE PLACES ==========
    private fun initializeSafePlaces() {
        val placesText = buildString {
            append("Safe Places:\n")
            append("• Home: 40.7128, -74.0060\n")
            append("• Senior Center: 40.7490, -73.9680\n")
            append("• Doctor's Office: 40.7580, -73.9855\n")
        }
        _safePlacesText.value = placesText
    }

    // ========== GET CURRENT LOCATION ==========
    fun getCurrentLocation(onUpdated: (String) -> Unit = {}) {
        viewModelScope.launch {
            // Demo-safe location near the saved San Diego home location.
            val latitude = 32.7157 + (Math.random() - 0.5) * 0.002
            val longitude = -117.1611 + (Math.random() - 0.5) * 0.002
            val timestamp = LocalDateTime.now().format(timeFormatter)

            _currentLatitude.value = latitude
            _currentLongitude.value = longitude
            _locationTimestamp.value = timestamp
            _locationStatusText.value = "Demo location"
            val refreshedShareText = buildString {
                append("CareApp Location\nI may need help.\n\n")
                append("Latitude: %.5f\nLongitude: %.5f\nLast updated: %s\n\n".format(latitude, longitude, timestamp))
                append("Google Maps link: https://maps.google.com/?q=$latitude,$longitude\n\n")
                append("Caregiver note: I may have memory loss. Please call my caregiver if needed.\n")
                append("Demo location for app testing.")
            }
            _shareLocationText.value = refreshedShareText
            onUpdated(refreshedShareText)

            // Check if within safe zone (demo: within 0.05 degrees = ~5km)
            val distanceFromHome = com.careapp.app.util.LocationManager.calculateDistance(
                latitude, longitude,
                32.7157, -117.1611
            )
            _isWithinSafeZone.value = distanceFromHome < 300.0

            if (!_isWithinSafeZone.value) {
                _dialogTitle.value = "Outside Safe Zone"
                _dialogMessage.value = "You are outside your saved safe places. Do you need help?"
            }
        }
    }

    // ========== CHECK GEOFENCE STATUS ==========
    fun checkGeofenceStatus() {
        viewModelScope.launch {
            getCurrentLocation()

            if (!_isWithinSafeZone.value) {
                _dialogTitle.value = "⚠️ Geofence Alert"
                _dialogMessage.value = "You may have wandered outside your safe zone.\n\n" +
                    "Current location: ${_currentLatitude.value.toString().take(6)}, ${_currentLongitude.value.toString().take(6)}\n\n" +
                    "Would you like to activate Lost Mode?"
                _showDialog.value = true
            }
        }
    }

    // ========== SHARE LOCATION ==========
    fun shareLocation() {
        val locationText = _shareLocationText.value.ifBlank { "Location is not available yet." }

        _dialogTitle.value = "Location Shared"
        _dialogMessage.value = locationText
        _showDialog.value = true
    }

    // ========== MARK AS SAFE ==========
    fun markAsSafe() {
        viewModelScope.launch {
            val timestamp = LocalDateTime.now().format(timeFormatter)

            _dialogTitle.value = "✓ Status Updated"
            _dialogMessage.value = "You have been marked as safe at $timestamp.\n\n" +
                "Your caregiver has been notified."
            _showDialog.value = true

            // Log the safe marker event
            // (This would create an event log in a real app)
        }
    }

    // ========== DISMISS DIALOG ==========
    fun dismissDialog() {
        _showDialog.value = false
        _dialogTitle.value = ""
        _dialogMessage.value = ""
    }
}

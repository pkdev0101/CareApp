package com.careapp.app.data

data class EmergencyTimelineEntry(
    val id: Long,
    val timestamp: String,
    val title: String,
    val description: String,
    val severity: String = "Info"
)

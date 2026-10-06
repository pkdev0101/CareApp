package com.careapp.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class Medication(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val dose: String,
    val scheduledTime: String,
    val instructions: String,
    val pillDescription: String,
    val reason: String,
    val doctor: String,
    val caregiverConfirmationRequired: Boolean = false,
    val frequencyMinutes: Int = 1440
)

@Entity(tableName = "medication_logs")
data class MedicationLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medicationId: Int,
    val date: String,
    val scheduledTime: String,
    val status: String, // Pending, Taken, Skipped, Missed, Needs Help
    val completedTime: String? = null,
    val notes: String = ""
)

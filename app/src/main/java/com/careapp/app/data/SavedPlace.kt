package com.careapp.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_places")
data class SavedPlace(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String, // Home, Caregiver home, Senior center, Hospital, Safe place, Risky place
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val notes: String = ""
)

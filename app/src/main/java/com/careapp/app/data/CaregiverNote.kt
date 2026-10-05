package com.careapp.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "caregiver_notes")
data class CaregiverNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val mealNotes: String = "",
    val medicationNotes: String = "",
    val mood: String = "",
    val sleepQuality: String = "",
    val confusionNotes: String = "",
    val safetyNotes: String = "",
    val repeatedQuestions: String = "",
    val nextSteps: String = ""
)

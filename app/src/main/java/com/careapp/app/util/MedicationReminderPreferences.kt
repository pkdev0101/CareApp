package com.careapp.app.util

import android.content.Context
import com.careapp.app.data.Medication

/** Stores the caregiver's explicit reminder choice without adding medical data to the database schema. */
class MedicationReminderPreferences(context: Context) {
    private val preferences = context.getSharedPreferences("medication_reminder_preferences", Context.MODE_PRIVATE)

    fun isEnabled(medication: Medication): Boolean =
        if (preferences.contains(key(medication))) preferences.getBoolean(key(medication), false) else true

    fun setEnabled(medication: Medication, enabled: Boolean) {
        preferences.edit().putBoolean(key(medication), enabled).apply()
    }

    private fun key(medication: Medication): String = "enabled_${if (medication.id != 0) medication.id else medication.name.trim().lowercase()}"
}

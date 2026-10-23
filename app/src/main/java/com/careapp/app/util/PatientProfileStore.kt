package com.careapp.app.util

import android.content.Context

data class PatientProfileData(
    val name: String,
    val age: String,
    val dateOfBirth: String,
    val photoUri: String? = null,
    val fullName: String = name,
    val relationship: String = "",
    val memoryNote: String = "",
    val communicationStyle: String = ""
)

class PatientProfileStore(context: Context) {
    private val preferences = context.applicationContext
        .getSharedPreferences(if (DemoModeController.isActive(context)) "careapp_demo_patient_profile" else "careapp_patient_profile", Context.MODE_PRIVATE)

    fun load(): PatientProfileData {
        return PatientProfileData(
            name = preferences.getString("patient_name", null).orEmpty(),
            age = preferences.getString("patient_age", null).orEmpty(),
            dateOfBirth = preferences.getString("patient_dob", null).orEmpty(),
            photoUri = preferences.getString("patient_photo_uri", null)?.takeIf { it.isNotBlank() },
            fullName = preferences.getString("patient_full_name", null).orEmpty().ifBlank { preferences.getString("patient_name", null).orEmpty() },
            relationship = preferences.getString("patient_relationship", "").orEmpty(),
            memoryNote = preferences.getString("patient_memory_note", "").orEmpty(),
            communicationStyle = preferences.getString("patient_communication_style", "").orEmpty()
        )
    }

    fun save(profile: PatientProfileData) {
        preferences.edit()
            .putString("patient_name", profile.name.trim())
            .putString("patient_age", profile.age.trim())
            .putString("patient_dob", profile.dateOfBirth.trim())
            .putString("patient_photo_uri", profile.photoUri.orEmpty())
            .putString("patient_full_name", profile.fullName.trim())
            .putString("patient_relationship", profile.relationship.trim())
            .putString("patient_memory_note", profile.memoryNote.trim())
            .putString("patient_communication_style", profile.communicationStyle.trim())
            .apply()
    }

    private fun calculateAge(dateOfBirth: String): String {
        val parts = dateOfBirth.split("/")
        if (parts.size != 3) return ""
        val month = parts[0].toIntOrNull() ?: return ""
        val day = parts[1].toIntOrNull() ?: return ""
        val year = parts[2].toIntOrNull() ?: return ""
        val today = java.time.LocalDate.now()
        var age = today.year - year
        if (today < java.time.LocalDate.of(today.year, month, day)) age--
        return age.takeIf { it > 0 }?.toString().orEmpty()
    }
}

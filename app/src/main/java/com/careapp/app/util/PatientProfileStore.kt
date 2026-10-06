package com.careapp.app.util

import android.content.Context
import com.careapp.app.data.SampleData

data class PatientProfileData(
    val name: String,
    val age: String,
    val dateOfBirth: String,
    val photoUri: String? = null
)

class PatientProfileStore(context: Context) {
    private val preferences = context.applicationContext
        .getSharedPreferences("careapp_patient_profile", Context.MODE_PRIVATE)

    fun load(): PatientProfileData {
        val defaultProfile = SampleData.seniorProfile
        return PatientProfileData(
            name = preferences.getString("patient_name", null).orEmpty().ifBlank { defaultProfile.preferredName },
            age = preferences.getString("patient_age", null).orEmpty().ifBlank { calculateAge(defaultProfile.birthday) },
            dateOfBirth = preferences.getString("patient_dob", null).orEmpty().ifBlank { defaultProfile.birthday },
            photoUri = preferences.getString("patient_photo_uri", null)?.takeIf { it.isNotBlank() }
        )
    }

    fun save(profile: PatientProfileData) {
        preferences.edit()
            .putString("patient_name", profile.name.trim())
            .putString("patient_age", profile.age.trim())
            .putString("patient_dob", profile.dateOfBirth.trim())
            .putString("patient_photo_uri", profile.photoUri.orEmpty())
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

package com.careapp.app.util

import android.content.Context

data class EmergencyProfileData(
    val memoryNote: String,
    val allergies: String,
    val conditions: String,
    val emergencyContact: String,
    val preferredHospital: String,
    val calmingInstructions: String
)

class EmergencyProfileStore(context: Context) {
    private val prefs = context.getSharedPreferences(if (DemoModeController.isActive(context)) "careapp_demo_emergency_profile" else "emergency_profile", Context.MODE_PRIVATE)

    fun load() = EmergencyProfileData(
        prefs.getString("memory_note", "You may have memory loss. Please speak slowly and tell me where I am.") ?: "",
        prefs.getString("allergies", "") ?: "",
        prefs.getString("conditions", "") ?: "",
        prefs.getString("emergency_contact", "") ?: "",
        prefs.getString("preferred_hospital", "") ?: "",
        prefs.getString("calming_instructions", "") ?: ""
    )

    fun save(data: EmergencyProfileData) = prefs.edit()
        .putString("memory_note", data.memoryNote)
        .putString("allergies", data.allergies)
        .putString("conditions", data.conditions)
        .putString("emergency_contact", data.emergencyContact)
        .putString("preferred_hospital", data.preferredHospital)
        .putString("calming_instructions", data.calmingInstructions)
        .apply()

    fun hasProfile(): Boolean = listOf(
        "memory_note", "allergies", "conditions", "emergency_contact", "preferred_hospital", "calming_instructions"
    ).any { prefs.getString(it, "").orEmpty().isNotBlank() }
}

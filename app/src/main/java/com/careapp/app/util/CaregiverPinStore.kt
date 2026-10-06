package com.careapp.app.util

import android.content.Context

class CaregiverPinStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences("careapp_preferences", Context.MODE_PRIVATE)

    fun hasPin(): Boolean = !preferences.getString("caregiver_pin", null).isNullOrBlank()

    fun setPin(pin: String) {
        val editor = preferences.edit().putString("caregiver_pin", pin)
        if (preferences.getString("supervisor_pin", null).isNullOrBlank()) {
            editor.putString("supervisor_pin", pin)
        }
        editor.apply()
    }

    fun matches(pin: String): Boolean = preferences.getString("caregiver_pin", null) == pin

    fun setCaregiverPin(pin: String) {
        preferences.edit().putString("caregiver_pin", pin).apply()
    }

    fun matchesSupervisor(pin: String): Boolean {
        val supervisorPin = preferences.getString("supervisor_pin", null)
            ?: preferences.getString("caregiver_pin", null)
        return supervisorPin == pin
    }
}

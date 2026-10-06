package com.careapp.app.util

import android.content.Context

class CaregiverPinStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences("careapp_preferences", Context.MODE_PRIVATE)

    fun hasPin(): Boolean = !preferences.getString("caregiver_pin", null).isNullOrBlank()

    fun setPin(pin: String) {
        preferences.edit().putString("caregiver_pin", pin).apply()
    }

    fun matches(pin: String): Boolean = preferences.getString("caregiver_pin", null) == pin
}

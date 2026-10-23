package com.careapp.app.util

import android.content.Context

class OnboardingStore(context: Context) {
    private val preferences = context.applicationContext
        .getSharedPreferences("careapp_onboarding", Context.MODE_PRIVATE)

    fun isCompleted(): Boolean = preferences.getBoolean("onboarding_completed", false)

    fun currentStep(): Int = preferences.getInt("onboarding_step", 0).coerceIn(0, 5)

    fun markStarted() { preferences.edit().putBoolean("onboarding_started", true).apply() }

    fun hasSavedProgress(): Boolean = preferences.getBoolean("onboarding_started", false)

    fun saveStep(step: Int) {
        preferences.edit().putInt("onboarding_step", step.coerceIn(0, 5)).apply()
    }

    fun markCompleted() {
        preferences.edit()
            .putBoolean("onboarding_completed", true)
            .putInt("onboarding_step", 5)
            .apply()
    }

    fun setDemoModeEnabled(enabled: Boolean) {
        preferences.edit().putBoolean("demo_mode_enabled", enabled).apply()
    }

    fun isDemoModeEnabled(): Boolean = preferences.getBoolean("demo_mode_enabled", false)
}

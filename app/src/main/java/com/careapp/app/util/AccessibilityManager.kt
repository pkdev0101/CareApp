package com.careapp.app.util

import android.content.Context
import android.content.SharedPreferences

object AccessibilityManager {

    private const val PREFS_NAME = "careanchor_accessibility"
    private const val KEY_TEXT_SIZE = "text_size_multiplier"
    private const val KEY_HIGH_CONTRAST = "high_contrast_mode"
    private const val KEY_SIMPLE_MODE = "simple_mode"
    private const val KEY_SOUND_ENABLED = "sound_enabled"
    private const val KEY_VIBRATION_ENABLED = "vibration_enabled"

    fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun setTextSizeMultiplier(context: Context, multiplier: Float) {
        getPreferences(context).edit().apply {
            putFloat(KEY_TEXT_SIZE, multiplier)
            apply()
        }
    }

    fun getTextSizeMultiplier(context: Context): Float {
        return getPreferences(context).getFloat(KEY_TEXT_SIZE, 1.0f)
    }

    fun setHighContrast(context: Context, enabled: Boolean) {
        getPreferences(context).edit().apply {
            putBoolean(KEY_HIGH_CONTRAST, enabled)
            apply()
        }
    }

    fun isHighContrastMode(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_HIGH_CONTRAST, false)
    }

    fun setSimpleMode(context: Context, enabled: Boolean) {
        getPreferences(context).edit().apply {
            putBoolean(KEY_SIMPLE_MODE, enabled)
            apply()
        }
    }

    fun isSimpleMode(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_SIMPLE_MODE, false)
    }

    fun setSoundEnabled(context: Context, enabled: Boolean) {
        getPreferences(context).edit().apply {
            putBoolean(KEY_SOUND_ENABLED, enabled)
            apply()
        }
    }

    fun isSoundEnabled(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_SOUND_ENABLED, true)
    }

    fun setVibrationEnabled(context: Context, enabled: Boolean) {
        getPreferences(context).edit().apply {
            putBoolean(KEY_VIBRATION_ENABLED, enabled)
            apply()
        }
    }

    fun isVibrationEnabled(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_VIBRATION_ENABLED, true)
    }

    fun resetToDefaults(context: Context) {
        getPreferences(context).edit().apply {
            putFloat(KEY_TEXT_SIZE, 1.0f)
            putBoolean(KEY_HIGH_CONTRAST, false)
            putBoolean(KEY_SIMPLE_MODE, false)
            putBoolean(KEY_SOUND_ENABLED, true)
            putBoolean(KEY_VIBRATION_ENABLED, true)
            apply()
        }
    }

    fun getAccessibilitySummary(context: Context): String {
        val textSize = getTextSizeMultiplier(context)
        val highContrast = isHighContrastMode(context)
        val simpleMode = isSimpleMode(context)
        val sound = isSoundEnabled(context)
        val vibration = isVibrationEnabled(context)

        return "Accessibility Settings:\n" +
            "Text Size: ${(textSize * 100).toInt()}%\n" +
            "High Contrast: ${if (highContrast) "On" else "Off"}\n" +
            "Simple Mode: ${if (simpleMode) "On" else "Off"}\n" +
            "Sound: ${if (sound) "On" else "Off"}\n" +
            "Vibration: ${if (vibration) "On" else "Off"}"
    }

    fun getDemoResetInstructions(): String {
        return """
            DEMO DATA RESET INSTRUCTIONS

            To reset the app to demo state:

            1. Open Settings (bottom navigation)
            2. Tap "Reset Demo Data"
            3. Confirm when prompted

            This will:
            ✓ Clear all medication logs
            ✓ Reset routine tasks
            ✓ Clear caregiver notes
            ✓ Restore sample medications
            ✓ Keep settings unchanged

            Time: ~2 seconds
        """.trimIndent()
    }

}

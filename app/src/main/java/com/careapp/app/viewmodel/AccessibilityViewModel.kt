package com.careapp.app.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AccessibilityViewModel(private val sharedPreferences: SharedPreferences? = null) : ViewModel() {

    enum class FontSize(val multiplier: Float) {
        NORMAL(1.0f),
        LARGE(1.2f),
        EXTRA_LARGE(1.4f)
    }

    // Font size preference
    private val fontSizePref = sharedPreferences?.getString("font_size", "NORMAL") ?: "NORMAL"
    private val _fontSize = MutableStateFlow(try {
        FontSize.valueOf(fontSizePref)
    } catch (e: Exception) {
        FontSize.NORMAL
    })
    val fontSize: StateFlow<FontSize> = _fontSize.asStateFlow()

    // Text size scale (0.9f to 1.4f)
    private val _textSizeScale = MutableStateFlow(sharedPreferences?.getFloat("text_size_scale", 1.0f) ?: 1.0f)
    val textSizeScale: StateFlow<Float> = _textSizeScale.asStateFlow()

    // High contrast mode
    private val _highContrast = MutableStateFlow(sharedPreferences?.getBoolean("high_contrast", false) ?: false)
    val highContrastEnabled: StateFlow<Boolean> = _highContrast.asStateFlow()

    // Large buttons mode
    private val _largeButtons = MutableStateFlow(sharedPreferences?.getBoolean("large_buttons", false) ?: false)
    val largeButtons: StateFlow<Boolean> = _largeButtons.asStateFlow()

    fun setFontSize(size: FontSize) {
        _fontSize.value = size
        sharedPreferences?.edit()?.putString("font_size", size.name)?.apply()
    }

    fun setTextSize(scale: Float) {
        val clampedScale = scale.coerceIn(0.8f, 1.4f)
        _textSizeScale.value = clampedScale
        sharedPreferences?.edit()?.putFloat("text_size_scale", clampedScale)?.apply()
    }

    fun setHighContrast(enabled: Boolean) {
        _highContrast.value = enabled
        sharedPreferences?.edit()?.putBoolean("high_contrast", enabled)?.apply()
    }

    fun setLargeButtons(enabled: Boolean) {
        _largeButtons.value = enabled
        sharedPreferences?.edit()?.putBoolean("large_buttons", enabled)?.apply()
    }
}

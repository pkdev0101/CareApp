package com.careapp.app.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeViewModel(private val sharedPreferences: SharedPreferences? = null) : ViewModel() {

    enum class AppThemeMode { SYSTEM, LIGHT, DARK }

    private val storedMode = sharedPreferences?.getString("theme_mode", AppThemeMode.SYSTEM.name) ?: AppThemeMode.SYSTEM.name
    private val _themeMode = MutableStateFlow(runCatching { AppThemeMode.valueOf(storedMode) }.getOrDefault(AppThemeMode.SYSTEM))
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        sharedPreferences?.edit()?.putString("theme_mode", mode.name)?.apply()
    }

    private val _isDarkMode = MutableStateFlow(sharedPreferences?.getBoolean("dark_mode", false) ?: false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        val newValue = !_isDarkMode.value
        _isDarkMode.value = newValue
        sharedPreferences?.edit()?.putBoolean("dark_mode", newValue)?.apply()
    }

    fun setDarkMode(isDark: Boolean) {
        _isDarkMode.value = isDark
        sharedPreferences?.edit()?.putBoolean("dark_mode", isDark)?.apply()
    }
}

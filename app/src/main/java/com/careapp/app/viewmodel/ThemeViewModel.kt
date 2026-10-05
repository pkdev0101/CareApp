package com.careapp.app.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeViewModel(private val sharedPreferences: SharedPreferences? = null) : ViewModel() {

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

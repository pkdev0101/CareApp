package com.careapp.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class CareMode { SENIOR, CAREGIVER }

class CareModeViewModel(context: Context) : ViewModel() {
    private val preferences = context.applicationContext.getSharedPreferences("careapp_preferences", Context.MODE_PRIVATE)
    private val _mode = MutableStateFlow(
        if (preferences.getString("care_mode", CareMode.SENIOR.name) == CareMode.CAREGIVER.name) CareMode.CAREGIVER else CareMode.SENIOR
    )
    val mode: StateFlow<CareMode> = _mode.asStateFlow()

    fun toggleMode() {
        val next = if (_mode.value == CareMode.SENIOR) CareMode.CAREGIVER else CareMode.SENIOR
        _mode.value = next
        preferences.edit().putString("care_mode", next.name).apply()
    }
}

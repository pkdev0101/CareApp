package com.careapp.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.careapp.app.data.repository.CareRepository

class MedicationViewModelFactory(private val repository: CareRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MedicationViewModel(repository) as T
    }
}

class MedicationHistoryViewModelFactory(private val repository: CareRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MedicationHistoryViewModel(repository) as T
    }
}

class RoutineViewModelFactory(private val repository: CareRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RoutineViewModel(repository) as T
    }
}

class EmergencyViewModelFactory(private val repository: CareRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EmergencyViewModel(repository) as T
    }
}

class LocationViewModelFactory(private val repository: CareRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LocationViewModel(repository) as T
    }
}

class MemoryCardViewModelFactory(private val repository: CareRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MemoryCardViewModel(repository) as T
    }
}

class CaregiverNotesViewModelFactory(private val repository: CareRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CaregiverNotesViewModel(repository) as T
    }
}

class DashboardViewModelFactory(private val repository: CareRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = DashboardViewModel(repository) as T
}

class ThemeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val preferences = context.applicationContext.getSharedPreferences("careapp_preferences", Context.MODE_PRIVATE)
        return ThemeViewModel(preferences) as T
    }
}

class AccessibilityViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val preferences = context.applicationContext.getSharedPreferences("careapp_preferences", Context.MODE_PRIVATE)
        return AccessibilityViewModel(preferences) as T
    }
}

class CareModeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CareModeViewModel(context) as T
    }
}

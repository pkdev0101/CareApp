package com.careapp.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.careapp.app.data.repository.CareRepository

class MedicationViewModelFactory(private val repository: CareRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MedicationViewModel(repository) as T
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

class ThemeViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ThemeViewModel() as T
    }
}

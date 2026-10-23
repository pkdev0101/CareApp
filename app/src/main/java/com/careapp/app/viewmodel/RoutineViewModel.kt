package com.careapp.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.careapp.app.data.RoutineTask
import com.careapp.app.data.repository.CareRepository
import com.careapp.app.util.ActiveRoutineStore
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RoutineViewModel(application: Application, private val repository: CareRepository) : AndroidViewModel(application) {
    private val activeRoutineStore = ActiveRoutineStore(application)
    private val _activeRoutineCategory = MutableStateFlow(activeRoutineStore.activeCategory())
    val activeRoutineCategory: StateFlow<String> = _activeRoutineCategory.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    // Morning tasks
    val morningTasks: StateFlow<List<RoutineTask>> = repository.getRoutineTasksByCategory("Morning")
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Afternoon tasks
    val afternoonTasks: StateFlow<List<RoutineTask>> = repository.getRoutineTasksByCategory("Afternoon")
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Evening tasks
    val eveningTasks: StateFlow<List<RoutineTask>> = repository.getRoutineTasksByCategory("Evening")
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allTasks: StateFlow<List<RoutineTask>> = repository.getAllRoutineTasks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun startRoutine(category: String) {
        activeRoutineStore.start(category)
        _activeRoutineCategory.value = category
    }

    fun stopRoutine() {
        activeRoutineStore.clear()
        _activeRoutineCategory.value = ""
    }

    // Dialog states
    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _dialogMessage = MutableStateFlow("")
    val dialogMessage: StateFlow<String> = _dialogMessage.asStateFlow()

    private val _dialogTitle = MutableStateFlow("")
    val dialogTitle: StateFlow<String> = _dialogTitle.asStateFlow()

    // ========== TOGGLE TASK COMPLETION ==========
    fun toggleTaskCompletion(task: RoutineTask) {
        viewModelScope.launch {
            val currentTime = LocalDateTime.now().format(timeFormatter)

            val updatedTask = task.copy(
                completed = !task.completed,
                completedTime = if (!task.completed) currentTime else null
            )

            repository.updateRoutineTask(updatedTask)

            // Show confirmation dialog
            if (updatedTask.completed) {
                _dialogTitle.value = "✓ Task Completed"
                _dialogMessage.value = "${updatedTask.title} marked complete at $currentTime."
            } else {
                _dialogTitle.value = "Task Uncompleted"
                _dialogMessage.value = "${updatedTask.title} marked incomplete."
            }
            _showDialog.value = true
        }
    }

    // ========== COMPLETE MULTIPLE TASKS ==========
    fun completeAllTasksForCategory(category: String) {
        viewModelScope.launch {
            val tasks = when (category) {
                "Morning" -> morningTasks.value
                "Afternoon" -> afternoonTasks.value
                "Evening" -> eveningTasks.value
                else -> emptyList()
            }

            val currentTime = LocalDateTime.now().format(timeFormatter)

            tasks.forEach { task ->
                if (!task.completed) {
                    val updatedTask = task.copy(
                        completed = true,
                        completedTime = currentTime
                    )
                    repository.updateRoutineTask(updatedTask)
                }
            }

            _dialogTitle.value = "✓ All Tasks Completed"
            _dialogMessage.value = "All $category tasks marked complete."
            _showDialog.value = true
        }
    }

    // ========== GET COMPLETION PERCENTAGE ==========
    fun getCompletionPercentage(tasks: List<RoutineTask>): Int {
        if (tasks.isEmpty()) return 0
        val completedCount = tasks.count { it.completed }
        return (completedCount * 100) / tasks.size
    }

    // ========== DISMISS DIALOG ==========
    fun dismissDialog() {
        _showDialog.value = false
        _dialogTitle.value = ""
        _dialogMessage.value = ""
    }
}

package com.careapp.app.util

import com.careapp.app.data.Medication
import com.careapp.app.data.MedicationLog
import com.careapp.app.data.MemoryCard
import com.careapp.app.data.RoutineTask

/** Small, readable rules for answers shown by Help Me Remember. */
object ContextEngine {
    fun nextRoutineTask(tasks: List<RoutineTask>, activeCategory: String): RoutineTask? =
        tasks.firstOrNull { it.category == activeCategory && !it.completed }

    fun routineProgressSummary(tasks: List<RoutineTask>, activeCategory: String): String {
        if (activeCategory.isBlank()) {
            return "You have not started a routine yet. Ask your caregiver to start one in My Routine."
        }

        val activeTasks = tasks.filter { it.category == activeCategory }
        val nextTask = activeTasks.firstOrNull { !it.completed }
            ?: return "You were working on $activeCategory. All saved steps are complete."
        val nextIndex = activeTasks.indexOfFirst { it.id == nextTask.id }
        val completedBeforeNext = activeTasks.take(nextIndex).lastOrNull { it.completed }
        val completedText = completedBeforeNext?.let { "You already completed ${it.title}. " }.orEmpty()
        return "You were working on $activeCategory. $completedText".trim()
    }

    fun medicationAnswer(medications: List<Medication>, logs: List<MedicationLog>): String {
        val medication = medications.firstOrNull()
            ?: return "Your caregiver has not added medication information yet."
        val log = logs.lastOrNull { it.medicationId == medication.id }
        return when {
            log?.status == "Taken" -> "${medication.name} was recorded as taken at ${log.completedTime ?: "an unknown time"}."
            log?.status == "Skipped" -> "${medication.name} was recorded as skipped. Ask your caregiver what to do next."
            log != null -> "${medication.name} has a recorded status of ${log.status.lowercase()}. Ask your caregiver before taking it."
            else -> "${medication.name} is scheduled for ${medication.scheduledTime}, but it has not been confirmed as taken. Ask your caregiver before taking medicine if you are unsure."
        }
    }

    fun contactAnswer(memoryCards: List<MemoryCard>): String =
        memoryCards.firstOrNull()?.let { "You can call ${it.name}, your ${it.relationship.lowercase()}." }
            ?: "Your caregiver has not added a contact yet."
}

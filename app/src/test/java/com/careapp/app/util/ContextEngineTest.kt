package com.careapp.app.util

import com.careapp.app.data.Medication
import com.careapp.app.data.MedicationLog
import com.careapp.app.data.MemoryCard
import com.careapp.app.data.RoutineTask
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ContextEngineTest {
    private val medication = Medication(id = 7, name = "Donepezil", dose = "5 mg", scheduledTime = "8:00 AM", instructions = "As prescribed", pillDescription = "", reason = "", doctor = "")

    @Test fun routineAnswerRequiresExplicitlyActiveCategory() {
        val tasks = listOf(
            RoutineTask(id = 1, title = "Brush teeth", category = "Getting Ready", suggestedTime = "Morning"),
            RoutineTask(id = 2, title = "Get dressed", category = "Getting Ready", suggestedTime = "Morning")
        )
        assertEquals(null, ContextEngine.nextRoutineTask(tasks, ""))
        assertEquals("Brush teeth", ContextEngine.nextRoutineTask(tasks, "Getting Ready")?.title)
    }

    @Test fun medicationAnswerDoesNotClaimUnknownDoseWasTaken() {
        val answer = ContextEngine.medicationAnswer(listOf(medication), emptyList())
        assertTrue(answer.contains("has not been confirmed as taken"))
    }

    @Test fun medicationAnswerUsesRecordedTakenLog() {
        val log = MedicationLog(medicationId = 7, date = "2026-10-17", scheduledTime = "8:00 AM", status = "Taken", completedTime = "08:12")
        assertEquals("Donepezil was recorded as taken at 08:12.", ContextEngine.medicationAnswer(listOf(medication), listOf(log)))
    }

    @Test fun contactAnswerUsesSavedPerson() {
        val card = MemoryCard(name = "Sarah", relationship = "Daughter", memoryCue = "", helpsWith = "", phone = "555-0100")
        assertEquals("You can call Sarah, your daughter.", ContextEngine.contactAnswer(listOf(card)))
    }
}

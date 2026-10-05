package com.careapp.app.util

import com.careapp.app.data.Medication
import com.careapp.app.data.MedicationLog
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object MedicationManager {
    private const val MIN_HOURS_BETWEEN_DOSES = 4

    fun canTakeMedication(
        medicationId: Int,
        medicationLogs: List<MedicationLog>,
        minHoursBetween: Int = MIN_HOURS_BETWEEN_DOSES
    ): Pair<Boolean, String> {
        val today = LocalDate.now().toString()
        val takenToday = medicationLogs.find {
            it.medicationId == medicationId &&
            it.date == today &&
            it.status == "Taken"
        }

        return if (takenToday != null && takenToday.completedTime != null) {
            val lastTakenTime = try {
                LocalDateTime.parse(takenToday.completedTime, DateTimeFormatter.ISO_DATE_TIME)
            } catch (e: Exception) {
                LocalDateTime.parse("${takenToday.date}T${takenToday.completedTime}:00")
            }
            val hoursSinceLastDose = java.time.temporal.ChronoUnit.HOURS.between(
                lastTakenTime,
                LocalDateTime.now()
            )

            if (hoursSinceLastDose < minHoursBetween) {
                Pair(
                    false,
                    "This medication was already taken at ${takenToday.completedTime}.\n" +
                    "It is safe to take again in ${minHoursBetween - hoursSinceLastDose} hours.\n" +
                    "Ask your caregiver before taking it again."
                )
            } else {
                Pair(true, "Safe to take")
            }
        } else {
            Pair(true, "Safe to take")
        }
    }

    fun markMedicationTaken(
        medicationId: Int,
        medication: Medication,
        date: String = LocalDate.now().toString()
    ): MedicationLog {
        return MedicationLog(
            medicationId = medicationId,
            date = date,
            scheduledTime = medication.scheduledTime,
            status = "Taken",
            completedTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
            notes = "Taken by senior"
        )
    }

    fun getMissedMedicationWarning(missedCount: Int): String {
        return when {
            missedCount == 0 -> "No missed medications this week!"
            missedCount == 1 -> "⚠️ 1 missed medication this week"
            missedCount >= 5 -> "🚨 $missedCount missed medications this week - contact doctor"
            else -> "⚠️ $missedCount missed medications this week"
        }
    }

    fun generateMedicationSummary(logs: List<MedicationLog>): String {
        val taken = logs.count { it.status == "Taken" }
        val missed = logs.count { it.status == "Missed" }
        val skipped = logs.count { it.status == "Skipped" }

        return "This week:\n" +
            "✓ $taken medications taken\n" +
            "✗ $missed medications missed\n" +
            "⊘ $skipped medications skipped"
    }
}

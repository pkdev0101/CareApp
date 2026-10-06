package com.careapp.app.util

import com.careapp.app.data.CaregiverNote
import com.careapp.app.data.Medication
import com.careapp.app.data.MedicationLog
import com.careapp.app.data.SeniorProfile
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DoctorSummaryBuilder {

    data class MedicationReportCounts(
        val expected: Int,
        val taken: Int,
        val missed: Int,
        val skipped: Int
    )

    fun generateWeeklyReport(
        senior: SeniorProfile,
        medications: List<Medication>,
        medicationLogs: List<MedicationLog>,
        caregiverNotes: List<CaregiverNote>
    ): String {
        val medicationCounts = calculateWeeklyMedicationCounts(medications, medicationLogs)
        val missedCount = medicationCounts.missed
        val takenCount = medicationCounts.taken
        val skippedCount = medicationCounts.skipped
        val adherence = if (medicationCounts.expected == 0) 0 else (takenCount * 100 / medicationCounts.expected)
        val confusionEpisodes = caregiverNotes.count { it.confusionNotes.isNotEmpty() }

        return """
            ═══════════════════════════════════════════
            WEEKLY CARE REPORT
            ═══════════════════════════════════════════

            Patient: ${senior.fullName}
            Age: ${calculateAge(senior.birthday)}
            Report Period: Last 7 days
            Generated: ${LocalDate.now()}

            ─────────────────────────────────────────
            MEDICATION COMPLIANCE
            ─────────────────────────────────────────
            Total Medications: ${medications.size}
            Expected Doses: ${medicationCounts.expected}
            Doses Taken: $takenCount
            Doses Missed: ⚠️ $missedCount
            Doses Skipped: $skippedCount
            Adherence: $adherence%

            ${if (missedCount > 0) "CONCERN: $missedCount missed doses this week" else "✓ Good medication compliance"}

            Current Medications:
            ${medications.joinToString("\n") { "  • ${it.name} ${it.dose} - ${it.reason}" }}

            ─────────────────────────────────────────
            COGNITIVE & BEHAVIORAL
            ─────────────────────────────────────────
            Confusion Episodes: $confusionEpisodes

            ${if (confusionEpisodes > 0) {
                "Recent Episodes:\n" +
                caregiverNotes
                    .filter { it.confusionNotes.isNotEmpty() }
                    .takeLast(3)
                    .joinToString("\n") { "  • ${it.date}: ${it.confusionNotes}" }
            } else {
                "✓ No significant confusion episodes reported"
            }}

            ─────────────────────────────────────────
            CAREGIVER OBSERVATIONS
            ─────────────────────────────────────────
            ${if (caregiverNotes.isNotEmpty()) {
                val latest = caregiverNotes.last()
                "Latest Notes (${latest.date}):\n" +
                "  Mood: ${latest.mood}\n" +
                "  Sleep: ${latest.sleepQuality}\n" +
                "  Safety: ${latest.safetyNotes}\n" +
                "  Meals: ${latest.mealNotes}"
            } else {
                "No caregiver notes available"
            }}

            ─────────────────────────────────────────
            MEMORY / CONFUSION SUMMARY
            ─────────────────────────────────────────
            Confusion episodes: $confusionEpisodes
            ${caregiverNotes.joinToString("\n") { "• ${it.confusionNotes.ifBlank { "No confusion note recorded" }}" }}

            ─────────────────────────────────────────
            MOOD & SLEEP SUMMARY
            ─────────────────────────────────────────
            ${caregiverNotes.joinToString("\n") { "• Mood: ${it.mood}; Sleep: ${it.sleepQuality}" }}

            ─────────────────────────────────────────
            SAFETY SUMMARY
            ─────────────────────────────────────────
            ${caregiverNotes.joinToString("\n") { "• ${it.safetyNotes}" }}
            Wandering/lost events: Not recorded in this local report.

            ─────────────────────────────────────────
            CAREGIVER CONCERNS
            ─────────────────────────────────────────
            ${if (missedCount > 0 || confusionEpisodes > 0) generateConcernsSummary(missedCount, confusionEpisodes, 0) else "No concerns recorded this week."}

            QUESTIONS FOR DOCTOR
            • Are any medication times or reminders worth reviewing?
            • What should caregivers watch for if confusion increases?

            ─────────────────────────────────────────
            RECOMMENDATIONS
            ─────────────────────────────────────────
            ${generateRecommendations(missedCount, confusionEpisodes, medications.size)}

            ─────────────────────────────────────────
            DISCLAIMER
            ─────────────────────────────────────────
            This report is for organization and caregiver communication only.
            It does not diagnose or treat medical conditions.
            For medical concerns, contact ${senior.doctorName} at ${senior.doctorPhone}.

            For emergencies, call 911.

            ═══════════════════════════════════════════
        """.trimIndent()
    }

    /**
     * Counts scheduled doses for the current seven-day report window. An
     * as-needed medication has no expected dose. A past scheduled occurrence
     * without a recorded outcome is considered missed after the same 30-minute
     * grace period used by the medication history screen.
     */
    fun calculateWeeklyMedicationCounts(
        medications: List<Medication>,
        medicationLogs: List<MedicationLog>,
        now: LocalDateTime = LocalDateTime.now()
    ): MedicationReportCounts {
        val firstDate = now.toLocalDate().minusDays(6)
        val lastDate = now.toLocalDate()
        var expected = 0
        var taken = 0
        var missed = 0
        var skipped = 0

        medications.forEach { medication ->
            val startTime = parseScheduledTime(medication.scheduledTime) ?: return@forEach
            val intervalMinutes = medication.frequencyMinutes.coerceAtLeast(1)
            val medicationLogs = medicationLogs.filter { log ->
                log.medicationId == medication.id &&
                    runCatching { LocalDate.parse(log.date) }.getOrNull()?.let { it >= firstDate && it <= lastDate } == true
            }

            var date = firstDate
            while (!date.isAfter(lastDate)) {
                val dayStart = date.atTime(startTime)
                var occurrence = dayStart
                var dueOccurrences = 0
                while (occurrence.toLocalDate() == date && !occurrence.isAfter(now)) {
                    if (!occurrence.plusMinutes(30).isAfter(now)) dueOccurrences++
                    occurrence = occurrence.plusMinutes(intervalMinutes.toLong())
                }

                expected += dueOccurrences
                val dayLogs = medicationLogs.filter { it.date == date.toString() }
                val takenLogs = dayLogs.count { it.status == "Taken" }.coerceAtMost(dueOccurrences)
                val skippedLogs = dayLogs.count { it.status == "Skipped" }.coerceAtMost((dueOccurrences - takenLogs).coerceAtLeast(0))
                val helpLogs = dayLogs.count { it.status == "Needs Help" || it.status == "Need Help" }
                    .coerceAtMost((dueOccurrences - takenLogs - skippedLogs).coerceAtLeast(0))
                val explicitMissed = dayLogs.count { it.status == "Missed" }
                    .coerceAtMost((dueOccurrences - takenLogs - skippedLogs - helpLogs).coerceAtLeast(0))

                taken += takenLogs
                skipped += skippedLogs
                missed += explicitMissed + (dueOccurrences - takenLogs - skippedLogs - helpLogs - explicitMissed).coerceAtLeast(0)
                date = date.plusDays(1)
            }
        }

        return MedicationReportCounts(expected, taken, missed, skipped)
    }

    private fun parseScheduledTime(value: String): LocalTime? = runCatching {
        LocalTime.parse(value.trim().uppercase(), DateTimeFormatter.ofPattern("h:mm a"))
    }.getOrNull()

    private fun calculateAge(birthday: String): String {
        return try {
            val parts = birthday.split("/")
            if (parts.size == 3) {
                val birthYear = parts[2].toInt()
                val currentYear = LocalDate.now().year
                (currentYear - birthYear).toString()
            } else {
                "Unknown"
            }
        } catch (e: Exception) {
            "Unknown"
        }
    }

    private fun generateRecommendations(
        missedCount: Int,
        confusionEpisodes: Int,
        medicationCount: Int
    ): String {
        val recommendations = mutableListOf<String>()

        if (missedCount > 3) {
            recommendations.add("  ⚠️ Increase medication reminders or caregiver supervision")
        }

        if (confusionEpisodes > 2) {
            recommendations.add("  ⚠️ Consider cognitive assessment")
            recommendations.add("  ⚠️ Reinforce daily orientation routine")
        }

        if (medicationCount > 5) {
            recommendations.add("  💊 Consider medication audit for simplification")
        }

        if (recommendations.isEmpty()) {
            recommendations.add("  ✓ Continue current care plan")
        }

        return recommendations.joinToString("\n")
    }

    fun generateMedicationSummary(logs: List<MedicationLog>): String {
        val byStatus = logs.groupingBy { it.status }.eachCount()
        return "Medication Adherence This Week:\n" +
            "✓ Taken: ${byStatus["Taken"] ?: 0}\n" +
            "✗ Missed: ${byStatus["Missed"] ?: 0}\n" +
            "⊘ Skipped: ${byStatus["Skipped"] ?: 0}"
    }

    fun generateConcernsSummary(
        missedMeds: Int,
        confusionEpisodes: Int,
        falls: Int
    ): String {
        val concerns = mutableListOf<String>()
        if (missedMeds > 0) concerns.add("Missed medications: $missedMeds")
        if (confusionEpisodes > 0) concerns.add("Confusion episodes: $confusionEpisodes")
        if (falls > 0) concerns.add("Falls or near-falls: $falls")

        return if (concerns.isEmpty()) {
            "No significant concerns this week"
        } else {
            concerns.joinToString("\n", "Concerns to discuss:\n")
        }
    }
}

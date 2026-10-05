package com.careapp.app.util

import com.careapp.app.data.CaregiverNote
import com.careapp.app.data.Medication
import com.careapp.app.data.MedicationLog
import com.careapp.app.data.SeniorProfile
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object DoctorSummaryBuilder {

    fun generateWeeklyReport(
        senior: SeniorProfile,
        medications: List<Medication>,
        medicationLogs: List<MedicationLog>,
        caregiverNotes: List<CaregiverNote>
    ): String {
        val missedCount = medicationLogs.count { it.status == "Missed" }
        val takenCount = medicationLogs.count { it.status == "Taken" }
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
            Doses Taken: $takenCount
            Doses Missed: ⚠️ $missedCount

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

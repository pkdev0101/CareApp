package com.careapp.app.util

import com.careapp.app.data.SeniorProfile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object EmergencyManager {

    fun generateEmergencyCard(senior: SeniorProfile): String {
        return """
            EMERGENCY INFORMATION

            Name: ${senior.fullName}
            Preferred Name: ${senior.preferredName}
            Date of Birth: ${senior.birthday}

            MEMORY STATUS:
            ${senior.memoryNote}

            PRIMARY CAREGIVER:
            ${senior.primaryCaregiverName}
            Phone: ${senior.primaryCaregiverPhone}

            MEDICAL INFORMATION:
            Doctor: ${senior.doctorName}
            Phone: ${senior.doctorPhone}
            Hospital: ${senior.preferredHospital}

            CONDITIONS:
            ${senior.medicalConditions}

            ALLERGIES:
            ${senior.allergies}

            HOME ADDRESS:
            ${senior.address}

            CARE INSTRUCTIONS:
            ${senior.calmingInstructions}

            Generated: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"))}
        """.trimIndent()
    }

    fun generateEmergencySummary(senior: SeniorProfile): String {
        return "My name is ${senior.fullName}.\n" +
            "I may have memory loss.\n" +
            "Please speak slowly and call my caregiver:\n" +
            "${senior.primaryCaregiverName}\n" +
            "${senior.primaryCaregiverPhone}"
    }

    fun simulateEmergencyAlert(alertType: String): String {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"))
        return when (alertType) {
            "FALL" -> "⚠️ ALERT: Fall detected at $timestamp\n" +
                "Countdown: 15 seconds\n" +
                "Tap 'I'm OK' to cancel alert"
            "LOST" -> "📍 ALERT: Senior may be lost\n" +
                "Last location update: $timestamp\n" +
                "Contacting caregiver..."
            "911" -> "🚨 DEMO MODE: 911 call initiated\n" +
                "Time: $timestamp\n" +
                "In real app: emergency services would be contacted"
            else -> "⚠️ Emergency at $timestamp"
        }
    }

    fun getLocationCoordinatesForDemo(): Pair<Double, Double> {
        // Demo coordinates (San Diego area from plan)
        return Pair(32.7157, -117.1611)
    }

    fun formatCoordinates(latitude: Double, longitude: Double): String {
        return "📍 Current Location:\n" +
            "Latitude: $latitude\n" +
            "Longitude: $longitude\n" +
            "\nShow this to someone who can help."
    }
}

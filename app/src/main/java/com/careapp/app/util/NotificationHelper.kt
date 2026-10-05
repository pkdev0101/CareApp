package com.careapp.app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.careapp.app.R

object NotificationHelper {

    const val MEDICATION_CHANNEL_ID = "medication_reminders"
    const val EMERGENCY_CHANNEL_ID = "emergency_alerts"
    const val ROUTINE_CHANNEL_ID = "routine_reminders"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Medication reminders
            val medicationChannel = NotificationChannel(
                MEDICATION_CHANNEL_ID,
                "Medication Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to take medications"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(medicationChannel)

            // Emergency alerts
            val emergencyChannel = NotificationChannel(
                EMERGENCY_CHANNEL_ID,
                "Emergency Alerts",
                NotificationManager.IMPORTANCE_MAX
            ).apply {
                description = "Critical emergency alerts"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(emergencyChannel)

            // Routine reminders
            val routineChannel = NotificationChannel(
                ROUTINE_CHANNEL_ID,
                "Routine Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily routine task reminders"
            }
            notificationManager.createNotificationChannel(routineChannel)
        }
    }

    fun buildMedicationNotification(
        context: Context,
        medicationName: String,
        time: String
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, MEDICATION_CHANNEL_ID)
            .setContentTitle("Time to take your medicine")
            .setContentText("$medicationName at $time")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
    }

    fun buildEmergencyNotification(
        context: Context,
        title: String,
        message: String
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, EMERGENCY_CHANNEL_ID)
            .setContentTitle("🚨 $title")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_MAX)
    }

    fun buildRoutineNotification(
        context: Context,
        taskName: String,
        suggestedTime: String
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, ROUTINE_CHANNEL_ID)
            .setContentTitle("Routine reminder")
            .setContentText("Time for: $taskName ($suggestedTime)")
            .setSmallIcon(android.R.drawable.ic_menu_agenda)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

    fun getMedicationReminderMessage(medicationName: String, dose: String): String {
        return "Time to take your medicine: $medicationName ($dose)"
    }

    fun getEmergencyCallbackMessage(caregiverName: String, phone: String): String {
        return "Call $caregiverName: $phone\n\n" +
            "If no answer, call 911"
    }
}

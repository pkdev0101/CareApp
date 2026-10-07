package com.careapp.app.util

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class MedicationReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NotificationHelper.createNotificationChannels(context)
        val name = intent.getStringExtra(EXTRA_MEDICATION_NAME) ?: "your medicine"
        val time = intent.getStringExtra(EXTRA_SCHEDULED_TIME) ?: "now"
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            runCatching {
                NotificationManagerCompat.from(context).notify(
                    name.hashCode(),
                    NotificationHelper.buildMedicationNotification(context, name, time).build()
                )
            }
        }
    }

    companion object {
        const val EXTRA_MEDICATION_NAME = "medication_name"
        const val EXTRA_SCHEDULED_TIME = "scheduled_time"
    }
}

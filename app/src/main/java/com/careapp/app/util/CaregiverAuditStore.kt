package com.careapp.app.util

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Local demo audit trail. This is an accountability aid, not authentication or medical authorization. */
class CaregiverAuditStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences("caregiver_audit", Context.MODE_PRIVATE)

    fun configureCaregiver(name: String, securityCode: String) {
        preferences.edit()
            .putString("caregiver_name", name.trim())
            .putString("security_code", securityCode.trim())
            .apply()
        record("Caregiver identity updated")
    }

    fun hasIdentity(): Boolean = !caregiverName().isNullOrBlank() && !preferences.getString("security_code", "").isNullOrBlank()

    fun caregiverName(): String? = preferences.getString("caregiver_name", null)

    fun record(action: String) {
        if (!hasIdentity() && action != "Caregiver identity updated") return
        // Use a sortable 24-hour timestamp so the newest audit entry is always first.
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        val code = preferences.getString("security_code", "").orEmpty()
        val codeLabel = if (code.length <= 4) code else code.takeLast(4)
        val entry = "$timestamp | ${caregiverName().orEmpty()} | code ****$codeLabel | $action"
        val existing = preferences.getStringSet("entries", emptySet()).orEmpty().toMutableSet()
        existing.add(entry)
        preferences.edit().putStringSet("entries", existing).apply()
    }

    fun recentEntries(): List<String> = preferences.getStringSet("entries", emptySet()).orEmpty()
        .sortedWith(compareByDescending { entryTimestamp(it) })
        .take(10)

    private fun entryTimestamp(entry: String): Date {
        val timestamp = entry.substringBefore(" | ")
        return runCatching {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).parse(timestamp)
        }.getOrElse {
            runCatching {
                SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.getDefault()).parse(timestamp)
            }.getOrElse { Date(0) }
        } ?: Date(0)
    }

    fun handoffSummary(): String = recentEntries().takeIf { it.isNotEmpty() }?.joinToString("\n")
        ?: "No caregiver audit entries recorded yet."
}

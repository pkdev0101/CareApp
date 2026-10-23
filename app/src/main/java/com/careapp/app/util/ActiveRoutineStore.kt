package com.careapp.app.util

import android.content.Context

/** Stores only the routine the senior explicitly started. It never infers activity from a schedule. */
class ActiveRoutineStore(context: Context) {
    private val preferences = context.applicationContext
        .getSharedPreferences("careapp_active_routine", Context.MODE_PRIVATE)

    fun activeCategory(): String = preferences.getString("active_category", "").orEmpty()

    fun start(category: String) {
        preferences.edit().putString("active_category", category.trim()).apply()
    }

    fun clear() {
        preferences.edit().remove("active_category").apply()
    }
}

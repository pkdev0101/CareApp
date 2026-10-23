package com.careapp.app.util

import android.content.Context

/** Persistent mode switch. Demo mode has its own Room file and preference files. */
object DemoModeController {
    private const val PREFS = "careapp_demo_state"
    private const val ACTIVE = "active"

    fun isActive(context: Context): Boolean = context.applicationContext
        .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .getBoolean(ACTIVE, false)

    fun enter(context: Context) = context.applicationContext
        .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .edit().putBoolean(ACTIVE, true).apply()

    fun exit(context: Context) = context.applicationContext
        .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .edit().putBoolean(ACTIVE, false).apply()
}

package com.careapp.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routine_tasks")
data class RoutineTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // Morning, Afternoon, Evening, Night
    val suggestedTime: String,
    val completed: Boolean = false,
    val completedTime: String? = null,
    val notes: String = ""
)

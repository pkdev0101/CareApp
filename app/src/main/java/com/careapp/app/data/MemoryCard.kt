package com.careapp.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memory_cards")
data class MemoryCard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val relationship: String,
    val memoryCue: String,
    val helpsWith: String,
    val phone: String,
    val photoUri: String? = null
)

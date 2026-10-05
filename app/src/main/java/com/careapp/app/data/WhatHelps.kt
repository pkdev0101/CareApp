package com.careapp.app.data

data class WhatHelpsEntry(
    val id: Long = System.currentTimeMillis(),
    val timestamp: Long = System.currentTimeMillis(),
    val calmingAction: String,
    val result: String,
    val notes: String = ""
)

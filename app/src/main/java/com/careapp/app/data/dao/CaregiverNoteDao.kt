package com.careapp.app.data.dao

import androidx.room.*
import com.careapp.app.data.CaregiverNote

@Dao
interface CaregiverNoteDao {
    @Query("SELECT COUNT(*) FROM caregiver_notes")
    suspend fun countNotes(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCaregiverNote(note: CaregiverNote)

    @Query("SELECT * FROM caregiver_notes ORDER BY date DESC")
    suspend fun getAllCaregiverNotes(): List<CaregiverNote>

    @Query("SELECT * FROM caregiver_notes WHERE date = :date")
    suspend fun getCaregiverNoteByDate(date: String): CaregiverNote?

    @Update
    suspend fun updateCaregiverNote(note: CaregiverNote)

}

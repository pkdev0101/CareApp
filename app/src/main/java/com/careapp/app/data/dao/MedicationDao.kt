package com.careapp.app.data.dao

import androidx.room.*
import com.careapp.app.data.Medication
import com.careapp.app.data.MedicationLog
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {
    @Query("SELECT COUNT(*) FROM medications")
    suspend fun countMedications(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: Medication)

    @Query("SELECT * FROM medications ORDER BY scheduledTime ASC")
    fun getAllMedications(): Flow<List<Medication>>

    @Query("SELECT * FROM medications ORDER BY scheduledTime ASC")
    suspend fun getAllMedicationsSync(): List<Medication>

    @Query("SELECT * FROM medications WHERE id = :medicationId")
    suspend fun getMedicationById(medicationId: Int): Medication?

    @Delete
    suspend fun deleteMedication(medication: Medication)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicationLog(log: MedicationLog)

    @Query("SELECT * FROM medication_logs WHERE date = :date ORDER BY scheduledTime ASC")
    suspend fun getMedicationLogsByDate(date: String): List<MedicationLog>

    @Query("SELECT * FROM medication_logs WHERE medicationId = :medicationId AND date = :date")
    suspend fun getMedicationLogForDay(medicationId: Int, date: String): MedicationLog?

    @Query("SELECT * FROM medication_logs WHERE status = 'Missed' ORDER BY date DESC LIMIT 10")
    suspend fun getRecentMissedMedications(): List<MedicationLog>

    @Query("SELECT * FROM medication_logs ORDER BY date DESC, completedTime DESC")
    suspend fun getAllMedicationLogs(): List<MedicationLog>
}

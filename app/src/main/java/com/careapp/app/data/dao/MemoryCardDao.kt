package com.careapp.app.data.dao

import androidx.room.*
import com.careapp.app.data.MemoryCard
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryCardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemoryCard(card: MemoryCard)

    @Query("SELECT * FROM memory_cards ORDER BY name ASC")
    fun getAllMemoryCards(): Flow<List<MemoryCard>>

    @Query("SELECT * FROM memory_cards ORDER BY name ASC")
    suspend fun getAllMemoryCardsSync(): List<MemoryCard>

    @Update
    suspend fun updateMemoryCard(card: MemoryCard)

    @Delete
    suspend fun deleteMemoryCard(card: MemoryCard)
}

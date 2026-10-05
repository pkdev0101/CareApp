package com.careapp.app.data.dao

import androidx.room.*
import com.careapp.app.data.MemoryCard
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryCardDao {
    @Query("SELECT COUNT(*) FROM memory_cards")
    suspend fun countMemoryCards(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemoryCard(card: MemoryCard)

    @Query("SELECT * FROM memory_cards ORDER BY name ASC")
    fun getAllMemoryCards(): Flow<List<MemoryCard>>

    @Query("SELECT * FROM memory_cards WHERE id = :cardId")
    suspend fun getMemoryCardById(cardId: Int): MemoryCard?

    @Update
    suspend fun updateMemoryCard(card: MemoryCard)

    @Delete
    suspend fun deleteMemoryCard(card: MemoryCard)
}

package com.careapp.app.data.dao

import androidx.room.*
import com.careapp.app.data.SavedPlace
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedPlaceDao {
    @Query("SELECT * FROM saved_places ORDER BY name ASC")
    fun getAllPlaces(): Flow<List<SavedPlace>>

    @Query("SELECT * FROM saved_places ORDER BY name ASC")
    suspend fun getAllPlacesSync(): List<SavedPlace>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: SavedPlace): Long

    @Update
    suspend fun updatePlace(place: SavedPlace)

    @Delete
    suspend fun deletePlace(place: SavedPlace)
}

package com.example.risaleezanvakticompose.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.risaleezanvakticompose.data.local.entities.SavedLocation
import kotlinx.coroutines.flow.Flow


@Dao
interface SavedLocationDao {

   @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: SavedLocation): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg locations: SavedLocation)

    @Update
    suspend fun update(location: SavedLocation)

   @Delete
    suspend fun delete(location: SavedLocation)

   @Query("SELECT * FROM saved_locations")
    fun getAllLocations(): Flow<List<SavedLocation>>

   @Query("SELECT * FROM saved_locations WHERE place_id = :placeId")
    suspend fun getLocationById(placeId: Int): SavedLocation?

   @Query("SELECT * FROM saved_locations WHERE is_current_location = 1 LIMIT 1")
    fun getCurrentLocation(): Flow<SavedLocation?>

    @Query("SELECT * FROM saved_locations WHERE is_favorite = 1 ORDER BY last_updated DESC")
    fun getFavoriteLocations(): Flow<List<SavedLocation>>

   @Query("SELECT * FROM saved_locations WHERE is_current_location = 0 ORDER BY last_updated DESC LIMIT 1")
    suspend fun getLastUsedLocation(): SavedLocation?

    @Query("SELECT * FROM saved_locations WHERE LOWER(place_name) LIKE '%' || LOWER(:searchQuery) || '%'")
    fun searchLocations(searchQuery: String): Flow<List<SavedLocation>>

   @Query("DELETE FROM saved_locations")
    suspend fun deleteAllLocations()

   @Transaction
    @Query("UPDATE saved_locations SET is_current_location = 0 WHERE is_current_location = 1")
    suspend fun clearCurrentLocationFlags()

  @Query("""
        UPDATE saved_locations 
        SET is_favorite = CASE 
            WHEN is_favorite = 1 THEN 0 
            ELSE 1 
        END,
        last_updated = :timestamp
        WHERE place_id = :placeId
    """)
    suspend fun toggleFavorite(placeId: Int, timestamp: Long = System.currentTimeMillis())

  @Query("UPDATE saved_locations SET last_updated = :timestamp WHERE place_id = :placeId")
    suspend fun updateLastUsed(placeId: Int, timestamp: Long = System.currentTimeMillis())
}
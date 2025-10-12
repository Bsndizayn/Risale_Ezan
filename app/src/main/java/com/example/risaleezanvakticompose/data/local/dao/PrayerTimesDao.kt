package com.example.risaleezanvakticompose.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.risaleezanvakticompose.data.local.entities.PrayerTimesEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface PrayerTimesDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prayerTimes: PrayerTimesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg prayerTimes: PrayerTimesEntity)

    @Query("SELECT * FROM prayer_times WHERE location_place_id = :placeId AND date = :date LIMIT 1")
    suspend fun getPrayerTimesForDate(placeId: Int, date: String): PrayerTimesEntity?

    @Query("""
        SELECT * FROM prayer_times 
        WHERE location_place_id = :placeId 
        AND date BETWEEN :startDate AND :endDate 
        ORDER BY date ASC
    """)
    fun getPrayerTimesRange(placeId: Int, startDate: String, endDate: String): Flow<List<PrayerTimesEntity>>

   @Query("SELECT * FROM prayer_times WHERE location_place_id = :placeId ORDER BY date ASC")
    fun getAllPrayerTimesForLocation(placeId: Int): Flow<List<PrayerTimesEntity>>

    @Query("DELETE FROM prayer_times WHERE date < :beforeDate")
    suspend fun deleteOldPrayerTimes(beforeDate: String)

    @Query("DELETE FROM prayer_times WHERE location_place_id = :placeId")
    suspend fun deletePrayerTimesForLocation(placeId: Int)

    @Query("DELETE FROM prayer_times")
    suspend fun deleteAllPrayerTimes()

   @Query("SELECT COUNT(*) FROM prayer_times")
    suspend fun getPrayerTimesCount(): Int

   @Query("SELECT EXISTS(SELECT 1 FROM prayer_times WHERE location_place_id = :placeId AND date > :afterDate)")
    suspend fun hasPrayerTimesAfterDate(placeId: Int, afterDate: String): Boolean

   @Query("SELECT * FROM prayer_times WHERE location_place_id IN (:placeIds) AND date = :date")
    suspend fun getPrayerTimesForMultipleLocations(placeIds: List<Int>, date: String): List<PrayerTimesEntity>

    @Transaction
    suspend fun deleteAllAndInsert(vararg prayerTimes: PrayerTimesEntity) {
        deleteAllPrayerTimes()
        insertAll(*prayerTimes)
    }
}

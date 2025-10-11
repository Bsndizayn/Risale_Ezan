package com.example.risaleezanvakticompose.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.risaleezanvakticompose.data.local.entities.CachedRegion

@Dao
interface CachedRegionDao {

    @Query("SELECT * FROM cached_regions WHERE country_code = :countryCode ORDER BY region_name ASC")
    suspend fun getRegionsByCountry(countryCode: String): List<CachedRegion>

    @Query("SELECT * FROM cached_regions WHERE country_code = :countryCode")
    suspend fun searchRegions(countryCode: String): List<CachedRegion>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(regions: List<CachedRegion>)

    @Query("SELECT COUNT(*) FROM cached_regions WHERE country_code = :countryCode")
    suspend fun getCountByCountry(countryCode: String): Int

    @Query("DELETE FROM cached_regions WHERE country_code = :countryCode")
    suspend fun deleteByCountry(countryCode: String)
}
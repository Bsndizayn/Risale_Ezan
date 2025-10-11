package com.example.risaleezanvakticompose.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.risaleezanvakticompose.data.local.entities.CachedCity

@Dao
interface CachedCityDao {

    @Query("SELECT * FROM cached_cities WHERE country_code = :countryCode AND region_name = :regionName ORDER BY city_name ASC")
    suspend fun getCitiesByRegion(countryCode: String, regionName: String): List<CachedCity>

    @Query("SELECT * FROM cached_cities WHERE country_code = :countryCode AND region_name = :regionName")
    suspend fun searchCities(countryCode: String, regionName: String): List<CachedCity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cities: List<CachedCity>)

    @Query("SELECT COUNT(*) FROM cached_cities WHERE country_code = :countryCode AND region_name = :regionName")
    suspend fun getCountByRegion(countryCode: String, regionName: String): Int

    @Query("DELETE FROM cached_cities WHERE country_code = :countryCode AND region_name = :regionName")
    suspend fun deleteByRegion(countryCode: String, regionName: String)
}
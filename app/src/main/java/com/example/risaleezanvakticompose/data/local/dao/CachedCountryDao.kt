package com.example.risaleezanvakticompose.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.risaleezanvakticompose.data.local.entities.CachedCountry

@Dao
interface CachedCountryDao {

    @Query("SELECT * FROM cached_countries ORDER BY name ASC")
    suspend fun getAllCountries(): List<CachedCountry>

    @Query("SELECT * FROM cached_countries")
    suspend fun searchCountries(): List<CachedCountry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(countries: List<CachedCountry>)

    @Query("SELECT COUNT(*) FROM cached_countries")
    suspend fun getCount(): Int

    @Query("DELETE FROM cached_countries")
    suspend fun deleteAll()
}
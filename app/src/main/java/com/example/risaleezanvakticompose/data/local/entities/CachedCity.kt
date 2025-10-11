package com.example.risaleezanvakticompose.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "cached_cities",
    primaryKeys = ["country_code", "region_name", "city_name"],
    indices = [
        Index(value = ["country_code"]),
        Index(value = ["country_code", "region_name"])
    ]
)
data class CachedCity(
    @ColumnInfo(name = "country_code")
    val countryCode: String,

    @ColumnInfo(name = "region_name")
    val regionName: String,

    @ColumnInfo(name = "city_name")
    val cityName: String,

    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis()
)
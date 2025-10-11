package com.example.risaleezanvakticompose.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "cached_regions",
    primaryKeys = ["country_code", "region_name"],
    indices = [Index(value = ["country_code"])]
)
data class CachedRegion(
    @ColumnInfo(name = "country_code")
    val countryCode: String,

    @ColumnInfo(name = "region_name")
    val regionName: String,

    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis()
)
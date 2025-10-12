package com.example.risaleezanvakticompose.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "saved_locations")
data class SavedLocation(

    @PrimaryKey
    @ColumnInfo(name = "place_id")
    val placeId: Int,


    @ColumnInfo(name = "place_name")
    val placeName: String,


    @ColumnInfo(name = "country")
    val country: String,

    @ColumnInfo(name = "region")
    val region: String,

    @ColumnInfo(name = "country_code")
    val countryCode: String,

    @ColumnInfo(name = "latitude")
    val latitude: Double,

    @ColumnInfo(name = "longitude")
    val longitude: Double,

    @ColumnInfo(name = "timezone")
    val timezone: String? = null,

    @ColumnInfo(name = "is_current_location")
    val isCurrentLocation: Boolean = false,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis()
)
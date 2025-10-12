package com.example.risaleezanvakticompose.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "prayer_times",
    foreignKeys = [
        ForeignKey(
            entity = SavedLocation::class,
            parentColumns = ["place_id"],
            childColumns = ["location_place_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["location_place_id"]),
        Index(value = ["date"]),
        Index(value = ["location_place_id", "date"])
    ]
)
data class PrayerTimesEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,


    @ColumnInfo(name = "location_place_id")
    val locationPlaceId: Int,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "imsak")
    val imsak: String,


    @ColumnInfo(name = "gunes")
    val gunes: String,


    @ColumnInfo(name = "ogle")
    val ogle: String,

    @ColumnInfo(name = "ikindi")
    val ikindi: String,


    @ColumnInfo(name = "aksam")
    val aksam: String,


    @ColumnInfo(name = "yatsi")
    val yatsi: String,

    @ColumnInfo(name = "calculation_method")
    val calculationMethod: String = "Turkey",

    @ColumnInfo(name = "timezone_offset")
    val timezoneOffset: Int = 180,

    @ColumnInfo(name = "fetched_at")
    val fetchedAt: Long = System.currentTimeMillis()
)
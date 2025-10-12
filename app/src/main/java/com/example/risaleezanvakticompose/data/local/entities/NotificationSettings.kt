package com.example.risaleezanvakticompose.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Kullanıcının namaz vakitleri için bildirim tercihlerini tutar.
 *
 * Her namaz vakti için:
 * - Bildirim açık mı kapalı mı?
 * - Hangi ses dosyası çalacak?
 * - Kaç dakika önce bildirim gelsin?
 */
@Entity(tableName = "notification_settings")
data class NotificationSettings(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int = 1,  // Tek kayıt olacak, singleton pattern

    @ColumnInfo(name = "imsak_enabled")
    val imsakEnabled: Boolean = true,

    @ColumnInfo(name = "imsak_sound")
    val imsakSound: String = "default_ezan",  // Ses dosyası adı

    @ColumnInfo(name = "imsak_minutes_before")
    val imsakMinutesBefore: Int = 0,  // Kaç dakika önce bildirim

    @ColumnInfo(name = "ogle_enabled")
    val ogleEnabled: Boolean = true,

    @ColumnInfo(name = "ogle_sound")
    val ogleSound: String = "default_ezan",

    @ColumnInfo(name = "ogle_minutes_before")
    val ogleMinutesBefore: Int = 0,

    @ColumnInfo(name = "ikindi_enabled")
    val ikindiEnabled: Boolean = true,

    @ColumnInfo(name = "ikindi_sound")
    val ikindiSound: String = "default_ezan",

    @ColumnInfo(name = "ikindi_minutes_before")
    val ikindiMinutesBefore: Int = 0,

    @ColumnInfo(name = "aksam_enabled")
    val aksamEnabled: Boolean = true,

    @ColumnInfo(name = "aksam_sound")
    val aksamSound: String = "default_ezan",

    @ColumnInfo(name = "aksam_minutes_before")
    val aksamMinutesBefore: Int = 0,

    @ColumnInfo(name = "yatsi_enabled")
    val yatsiEnabled: Boolean = true,

    @ColumnInfo(name = "yatsi_sound")
    val yatsiSound: String = "default_ezan",

    @ColumnInfo(name = "yatsi_minutes_before")
    val yatsiMinutesBefore: Int = 0,

    // Güneş vakti için bildirim genelde açılmaz ama opsiyonel
    @ColumnInfo(name = "gunes_enabled")
    val gunesEnabled: Boolean = false,

    @ColumnInfo(name = "gunes_sound")
    val gunesSound: String = "default_ezan",

    @ColumnInfo(name = "gunes_minutes_before")
    val gunesMinutesBefore: Int = 0
)
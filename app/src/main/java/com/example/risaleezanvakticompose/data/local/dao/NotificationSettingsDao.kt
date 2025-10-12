package com.example.risaleezanvakticompose.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.risaleezanvakticompose.data.local.entities.NotificationSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationSettingsDao {

    @Query("SELECT * FROM notification_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<NotificationSettings?>

    @Query("SELECT * FROM notification_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsOnce(): NotificationSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: NotificationSettings)

    @Update
    suspend fun updateSettings(settings: NotificationSettings)

    @Query("UPDATE notification_settings SET imsak_enabled = :enabled WHERE id = 1")
    suspend fun toggleImsak(enabled: Boolean)

    @Query("UPDATE notification_settings SET ogle_enabled = :enabled WHERE id = 1")
    suspend fun toggleOgle(enabled: Boolean)

    @Query("UPDATE notification_settings SET ikindi_enabled = :enabled WHERE id = 1")
    suspend fun toggleIkindi(enabled: Boolean)

    @Query("UPDATE notification_settings SET aksam_enabled = :enabled WHERE id = 1")
    suspend fun toggleAksam(enabled: Boolean)

    @Query("UPDATE notification_settings SET yatsi_enabled = :enabled WHERE id = 1")
    suspend fun toggleYatsi(enabled: Boolean)

    @Query("UPDATE notification_settings SET gunes_enabled = :enabled WHERE id = 1")
    suspend fun toggleGunes(enabled: Boolean)
}
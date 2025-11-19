package com.example.risaleezanvakticompose.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.risaleezanvakticompose.data.local.convertes.Converters
import com.example.risaleezanvakticompose.data.local.dao.*
import com.example.risaleezanvakticompose.data.local.entities.*

@Database(
    entities = [
        SavedLocation::class,
        PrayerTimesEntity::class,
        CachedCountry::class,
        CachedRegion::class,
        CachedCity::class,
        NotificationSettings::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun savedLocationDao(): SavedLocationDao
    abstract fun prayerTimesDao(): PrayerTimesDao
    abstract fun cachedCountryDao(): CachedCountryDao
    abstract fun cachedRegionDao(): CachedRegionDao
    abstract fun cachedCityDao(): CachedCityDao
    abstract fun notificationSettingsDao(): NotificationSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DATABASE_NAME = "risale_ezan_vakti_database"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = INSTANCE ?: buildDatabase(context).also {
                    INSTANCE = it
                }
                instance
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .build()
        }

        fun getTestInstance(context: Context): AppDatabase {
            return Room.inMemoryDatabaseBuilder(
                context,
                AppDatabase::class.java
            )
                .allowMainThreadQueries()
                .build()
        }
    }
}
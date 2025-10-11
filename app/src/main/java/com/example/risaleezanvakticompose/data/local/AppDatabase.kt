package com.example.risaleezanvakticompose.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.risaleezanvakticompose.data.local.dao.*
import com.example.risaleezanvakticompose.data.local.entities.*

@Database(
    entities = [
        SavedLocation::class,
        PrayerTimesEntity::class,
        CachedCountry::class,
        CachedRegion::class,
        CachedCity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun savedLocationDao(): SavedLocationDao
    abstract fun prayerTimesDao(): PrayerTimesDao
    abstract fun cachedCountryDao(): CachedCountryDao
    abstract fun cachedRegionDao(): CachedRegionDao
    abstract fun cachedCityDao(): CachedCityDao

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
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build()
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE IF EXISTS prayertimes")

                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS saved_locations (
                        place_id INTEGER PRIMARY KEY NOT NULL,
                        place_name TEXT NOT NULL,
                        country TEXT NOT NULL,
                        region TEXT NOT NULL,
                        country_code TEXT NOT NULL,
                        latitude REAL NOT NULL,
                        longitude REAL NOT NULL,
                        timezone TEXT,
                        is_current_location INTEGER NOT NULL DEFAULT 0,
                        is_favorite INTEGER NOT NULL DEFAULT 0,
                        last_updated INTEGER NOT NULL DEFAULT 0
                    )
                """)

                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS prayer_times (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        location_place_id INTEGER NOT NULL,
                        date TEXT NOT NULL,
                        imsak TEXT NOT NULL,
                        gunes TEXT NOT NULL,
                        ogle TEXT NOT NULL,
                        ikindi TEXT NOT NULL,
                        aksam TEXT NOT NULL,
                        yatsi TEXT NOT NULL,
                        calculation_method TEXT NOT NULL DEFAULT 'Turkey',
                        timezone_offset INTEGER NOT NULL DEFAULT 180,
                        fetched_at INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY(location_place_id) REFERENCES saved_locations(place_id) ON DELETE CASCADE
                    )
                """)

                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_prayer_times_location_place_id 
                    ON prayer_times(location_place_id)
                """)

                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_prayer_times_date 
                    ON prayer_times(date)
                """)

                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_prayer_times_location_date 
                    ON prayer_times(location_place_id, date)
                """)
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS cached_countries (
                        code TEXT PRIMARY KEY NOT NULL,
                        name TEXT NOT NULL,
                        cached_at INTEGER NOT NULL
                    )
                """)

                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS cached_regions (
                        country_code TEXT NOT NULL,
                        region_name TEXT NOT NULL,
                        cached_at INTEGER NOT NULL,
                        PRIMARY KEY(country_code, region_name)
                    )
                """)

                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_cached_regions_country_code 
                    ON cached_regions(country_code)
                """)

                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS cached_cities (
                        country_code TEXT NOT NULL,
                        region_name TEXT NOT NULL,
                        city_name TEXT NOT NULL,
                        cached_at INTEGER NOT NULL,
                        PRIMARY KEY(country_code, region_name, city_name)
                    )
                """)

                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_cached_cities_country_code 
                    ON cached_cities(country_code)
                """)

                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_cached_cities_country_region 
                    ON cached_cities(country_code, region_name)
                """)
            }
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
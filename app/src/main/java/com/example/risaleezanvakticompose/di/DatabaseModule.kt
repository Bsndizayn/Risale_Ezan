package com.example.risaleezanvakticompose.di

import android.content.Context
import com.example.risaleezanvakticompose.data.local.AppDatabase
import com.example.risaleezanvakticompose.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideSavedLocationDao(database: AppDatabase): SavedLocationDao {
        return database.savedLocationDao()
    }

    @Provides
    @Singleton
    fun providePrayerTimesDao(database: AppDatabase): PrayerTimesDao {
        return database.prayerTimesDao()
    }

    @Provides
    @Singleton
    fun provideCachedCountryDao(database: AppDatabase): CachedCountryDao {
        return database.cachedCountryDao()
    }

    @Provides
    @Singleton
    fun provideCachedRegionDao(database: AppDatabase): CachedRegionDao {
        return database.cachedRegionDao()
    }

    @Provides
    @Singleton
    fun provideCachedCityDao(database: AppDatabase): CachedCityDao {
        return database.cachedCityDao()
    }
}
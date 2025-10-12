package com.example.risaleezanvakticompose.di

import com.example.risaleezanvakticompose.data.local.dao.*
import com.example.risaleezanvakticompose.data.remote.ApiService
import com.example.risaleezanvakticompose.data.repository.PrayerTimesRepository
import com.example.risaleezanvakticompose.data.repository.TesbihatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providePrayerTimesRepository(
        apiService: ApiService,
        savedLocationDao: SavedLocationDao,
        prayerTimesDao: PrayerTimesDao,
        cachedCountryDao: CachedCountryDao,
        cachedRegionDao: CachedRegionDao,
        cachedCityDao: CachedCityDao
    ): PrayerTimesRepository {
        return PrayerTimesRepository(
            apiService,
            savedLocationDao,
            prayerTimesDao,
            cachedCountryDao,
            cachedRegionDao,
            cachedCityDao
        )
    }

    @Provides
    @Singleton
    fun provideTesbihatRepository(): TesbihatRepository {
        return TesbihatRepository()
    }
}
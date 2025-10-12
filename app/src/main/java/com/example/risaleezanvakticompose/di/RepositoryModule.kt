package com.example.risaleezanvakticompose.di

import android.content.Context
import com.example.risaleezanvakticompose.data.local.dao.CachedCityDao
import com.example.risaleezanvakticompose.data.local.dao.CachedCountryDao
import com.example.risaleezanvakticompose.data.local.dao.CachedRegionDao
import com.example.risaleezanvakticompose.data.local.dao.PrayerTimesDao
import com.example.risaleezanvakticompose.data.local.dao.SavedLocationDao
import com.example.risaleezanvakticompose.data.remote.ApiService
import com.example.risaleezanvakticompose.data.repository.PrayerTimesRepository
import com.example.risaleezanvakticompose.data.repository.TesbihatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideTesbihatRepository(
        @ApplicationContext context: Context
    ): TesbihatRepository {
        return TesbihatRepository(context)
    }
}
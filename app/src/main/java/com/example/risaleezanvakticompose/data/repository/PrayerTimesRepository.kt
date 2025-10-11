package com.example.risaleezanvakticompose.data.repository

import com.example.risaleezanvakticompose.data.local.dao.*
import com.example.risaleezanvakticompose.data.local.entities.*
import com.example.risaleezanvakticompose.data.model.CountriesItem
import com.example.risaleezanvakticompose.data.remote.ApiService
import com.example.risaleezanvakticompose.util.normalizeForSearch
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerTimesRepository @Inject constructor(
    private val apiService: ApiService,
    private val savedLocationDao: SavedLocationDao,
    private val prayerTimesDao: PrayerTimesDao,
    private val cachedCountryDao: CachedCountryDao,
    private val cachedRegionDao: CachedRegionDao,
    private val cachedCityDao: CachedCityDao
) {

    fun getAllLocations(): Flow<List<SavedLocation>> = savedLocationDao.getAllLocations()

    fun getCurrentLocation(): Flow<SavedLocation?> = savedLocationDao.getCurrentLocation()

    fun getFavoriteLocations(): Flow<List<SavedLocation>> = savedLocationDao.getFavoriteLocations()

    suspend fun getLocationById(placeId: Int): SavedLocation? = savedLocationDao.getLocationById(placeId)

    suspend fun saveLocation(location: SavedLocation) = savedLocationDao.insert(location)

    suspend fun updateLocation(location: SavedLocation) = savedLocationDao.update(location)

    suspend fun deleteLocation(location: SavedLocation) = savedLocationDao.delete(location)

    suspend fun toggleFavorite(placeId: Int) = savedLocationDao.toggleFavorite(placeId)

    suspend fun setCurrentLocation(placeId: Int) {
        savedLocationDao.clearCurrentLocationFlags()
        val location = savedLocationDao.getLocationById(placeId)
        location?.let {
            savedLocationDao.update(it.copy(isCurrentLocation = true))
        }
    }

    suspend fun getPrayerTimesForDate(placeId: Int, date: String): PrayerTimesEntity? {
        return prayerTimesDao.getPrayerTimesForDate(placeId, date)
    }

    fun getPrayerTimesRange(placeId: Int, startDate: String, endDate: String): Flow<List<PrayerTimesEntity>> {
        return prayerTimesDao.getPrayerTimesRange(placeId, startDate, endDate)
    }

    suspend fun savePrayerTimes(prayerTimes: PrayerTimesEntity) = prayerTimesDao.insert(prayerTimes)

    suspend fun savePrayerTimesBatch(prayerTimes: List<PrayerTimesEntity>) {
        prayerTimesDao.insertAll(*prayerTimes.toTypedArray())
    }

    suspend fun fetchAndSaveGpsLocation(lat: Double, lng: Double): Result<SavedLocation> {
        return try {
            val response = apiService.getNearbyPlaces(lat, lng)
            if (response.isSuccessful && response.body() != null) {
                val places = response.body()!!
                if (places.isNotEmpty()) {
                    val place = places.first()
                    val savedLocation = SavedLocation(
                        placeId = place.id,
                        placeName = place.city ?: "",
                        country = place.country ?: "",
                        region = place.region ?: "",
                        countryCode = place.countryCode ?: "",
                        latitude = place.lat,
                        longitude = place.lng,
                        timezone = place.timezone,
                        isCurrentLocation = true
                    )

                    savedLocationDao.clearCurrentLocationFlags()
                    savedLocationDao.insert(savedLocation)

                    Result.success(savedLocation)
                } else {
                    Result.failure(Exception("No nearby places found"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchAndSavePrayerTimes(
        placeId: Int,
        lat: Double,
        lng: Double,
        startDate: String,
        days: Int = 7
    ): Result<Unit> {
        return try {
            val response = apiService.getPrayerTimesForGPS(
                lat = lat,
                lng = lng,
                date = startDate,
                days = days
            )

            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                val prayerTimesList = data.times.map { (date, times) ->
                    PrayerTimesEntity(
                        locationPlaceId = placeId,
                        date = date,
                        imsak = times[0],
                        gunes = times[1],
                        ogle = times[2],
                        ikindi = times[3],
                        aksam = times[4],
                        yatsi = times[5]
                    )
                }

                prayerTimesDao.insertAll(*prayerTimesList.toTypedArray())
                Result.success(Unit)
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteOldPrayerTimes(beforeDate: String) {
        prayerTimesDao.deleteOldPrayerTimes(beforeDate)
    }

    suspend fun refreshPrayerTimes(placeId: Int, lat: Double, lng: Double): Result<Unit> {
        prayerTimesDao.deletePrayerTimesForLocation(placeId)
        return fetchAndSavePrayerTimes(placeId, lat, lng, LocalDate.now().toString(), 30)
    }

    suspend fun getCountries(forceRefresh: Boolean = false): Result<List<CountriesItem>> {
        return try {
            if (!forceRefresh) {
                val cachedCount = cachedCountryDao.getCount()
                if (cachedCount > 0) {
                    val cached = cachedCountryDao.getAllCountries()
                    val countries = cached.map { CountriesItem(code = it.code, name = it.name) }
                    return Result.success(countries)
                }
            }

            val response = apiService.getCountries()
            if (response.isSuccessful && response.body() != null) {
                val countries = response.body()!!

                val cachedCountries = countries.map {
                    CachedCountry(code = it.code, name = it.name)
                }
                cachedCountryDao.insertAll(cachedCountries)

                Result.success(countries)
            } else {
                Result.failure(Exception("Countries fetch failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            val cached = cachedCountryDao.getAllCountries()
            if (cached.isNotEmpty()) {
                val countries = cached.map { CountriesItem(code = it.code, name = it.name) }
                Result.success(countries)
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun searchCountries(query: String): Result<List<CountriesItem>> {
        return try {
            val normalizedQuery = query.normalizeForSearch()

            var allCountries = cachedCountryDao.searchCountries()

            if (allCountries.isEmpty()) {
                val fetchResult = getCountries(forceRefresh = true)
                if (fetchResult.isFailure) {
                    return Result.failure(fetchResult.exceptionOrNull() ?: Exception("Fetch failed"))
                }
                allCountries = cachedCountryDao.searchCountries()
            }

            val filtered = allCountries.filter { country ->
                country.name.normalizeForSearch().contains(normalizedQuery)
            }

            val countries = filtered.map { CountriesItem(code = it.code, name = it.name) }
            Result.success(countries)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getRegions(countryCode: String, countryName: String, forceRefresh: Boolean = false): Result<List<String>> {
        return try {
            if (!forceRefresh) {
                val cachedCount = cachedRegionDao.getCountByCountry(countryCode)
                if (cachedCount > 0) {
                    val cached = cachedRegionDao.getRegionsByCountry(countryCode)
                    return Result.success(cached.map { it.regionName })
                }
            }

            val response = apiService.getRegions(countryName)
            if (response.isSuccessful && response.body() != null) {
                val regions = response.body()!!

                val cachedRegions = regions.map {
                    CachedRegion(countryCode = countryCode, regionName = it)
                }
                cachedRegionDao.insertAll(cachedRegions)

                Result.success(regions)
            } else {
                Result.failure(Exception("Regions fetch failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            val cached = cachedRegionDao.getRegionsByCountry(countryCode)
            if (cached.isNotEmpty()) {
                Result.success(cached.map { it.regionName })
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun searchRegions(countryCode: String, countryName: String, query: String): Result<List<String>> {
        return try {
            val normalizedQuery = query.normalizeForSearch()

            var allRegions = cachedRegionDao.searchRegions(countryCode)

            if (allRegions.isEmpty()) {
                val fetchResult = getRegions(countryCode, countryName, forceRefresh = true)
                if (fetchResult.isFailure) {
                    return Result.failure(fetchResult.exceptionOrNull() ?: Exception("Fetch failed"))
                }
                allRegions = cachedRegionDao.searchRegions(countryCode)
            }

            val filtered = allRegions.filter { region ->
                region.regionName.normalizeForSearch().contains(normalizedQuery)
            }

            Result.success(filtered.map { it.regionName })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCities(
        countryCode: String,
        countryName: String,
        regionName: String,
        forceRefresh: Boolean = false
    ): Result<List<String>> {
        return try {
            if (!forceRefresh) {
                val cachedCount = cachedCityDao.getCountByRegion(countryCode, regionName)
                if (cachedCount > 0) {
                    val cached = cachedCityDao.getCitiesByRegion(countryCode, regionName)
                    return Result.success(cached.map { it.cityName })
                }
            }

            val response = apiService.getCities(countryName, regionName)
            if (response.isSuccessful && response.body() != null) {
                val cities = response.body()!!

                val cachedCities = cities.map {
                    CachedCity(countryCode = countryCode, regionName = regionName, cityName = it)
                }
                cachedCityDao.insertAll(cachedCities)

                Result.success(cities)
            } else {
                Result.failure(Exception("Cities fetch failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            val cached = cachedCityDao.getCitiesByRegion(countryCode, regionName)
            if (cached.isNotEmpty()) {
                Result.success(cached.map { it.cityName })
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun searchCities(
        countryCode: String,
        countryName: String,
        regionName: String,
        query: String
    ): Result<List<String>> {
        return try {
            val normalizedQuery = query.normalizeForSearch()

            var allCities = cachedCityDao.searchCities(countryCode, regionName)

            if (allCities.isEmpty()) {
                val fetchResult = getCities(countryCode, countryName, regionName, forceRefresh = true)
                if (fetchResult.isFailure) {
                    return Result.failure(fetchResult.exceptionOrNull() ?: Exception("Fetch failed"))
                }
                allCities = cachedCityDao.searchCities(countryCode, regionName)
            }

            val filtered = allCities.filter { city ->
                city.cityName.normalizeForSearch().contains(normalizedQuery)
            }

            Result.success(filtered.map { it.cityName })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addLocationByName(
        country: String,
        region: String,
        city: String
    ): Result<SavedLocation> {
        return try {
            val response = apiService.getCoordinates(country, region, city)

            if (response.isSuccessful && response.body() != null) {
                val coords = response.body()!!

                val placeId = "${coords.countryCode}_${region}_${city}".hashCode()

                val savedLocation = SavedLocation(
                    placeId = placeId,
                    placeName = city,
                    country = country,
                    region = region,
                    countryCode = coords.countryCode,
                    latitude = coords.latitude,
                    longitude = coords.longitude,
                    timezone = null,
                    isCurrentLocation = false,
                    isFavorite = false
                )

                savedLocationDao.insert(savedLocation)

                fetchAndSavePrayerTimes(
                    placeId = placeId,
                    lat = coords.latitude,
                    lng = coords.longitude,
                    startDate = LocalDate.now().toString(),
                    days = 30
                )

                Result.success(savedLocation)
            } else {
                Result.failure(Exception("Coordinates fetch failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
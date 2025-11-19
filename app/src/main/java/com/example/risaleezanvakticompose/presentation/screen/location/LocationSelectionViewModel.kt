package com.example.risaleezanvakticompose.presentation.screen.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.risaleezanvakticompose.data.local.entities.SavedLocation
import com.example.risaleezanvakticompose.data.model.CountriesItem
import com.example.risaleezanvakticompose.data.repository.PrayerTimesRepository
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SelectionStep {
    data object CountrySelection : SelectionStep()
    data class RegionSelection(val country: CountriesItem) : SelectionStep()
    data class CitySelection(val country: CountriesItem, val region: String) : SelectionStep()
}

sealed class SearchResult {
    data class Countries(val items: List<CountriesItem>) : SearchResult()
    data class Regions(val items: List<String>) : SearchResult()
    data class Cities(val items: List<String>) : SearchResult()
}

@HiltViewModel
class LocationSelectionViewModel @Inject constructor(
    private val repository: PrayerTimesRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val savedLocations = repository.getAllLocations()
    val currentLocation = repository.getCurrentLocation()

    private val _currentStep = MutableStateFlow<SelectionStep>(SelectionStep.CountrySelection)
    val currentStep: StateFlow<SelectionStep> = _currentStep.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<SearchResult?>(null)
    val searchResults: StateFlow<SearchResult?> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _locationAdded = MutableStateFlow(false)
    val locationAdded: StateFlow<Boolean> = _locationAdded.asStateFlow()

    private val _needsLocationPermission = MutableStateFlow(false)
    val needsLocationPermission: StateFlow<Boolean> = _needsLocationPermission.asStateFlow()

    private val _isLoadingGps = MutableStateFlow(false)
    val isLoadingGps: StateFlow<Boolean> = _isLoadingGps.asStateFlow()

    private val _locationSelected = MutableStateFlow(false)
    val locationSelected: StateFlow<Boolean> = _locationSelected.asStateFlow()

    private val _loadingMessage = MutableStateFlow<String?>(null)
    val loadingMessage: StateFlow<String?> = _loadingMessage.asStateFlow()

    init {
        loadCountries()
    }

    fun getDisplayName(country: CountriesItem): String {
        return when (country.code.uppercase()) {
            "TR" -> "Türkiye"
            else -> country.name
        }
    }

    private fun loadCountries() {
        viewModelScope.launch {
            _isSearching.value = true
            repository.getCountries().fold(
                onSuccess = { countries ->
                    _searchResults.value = SearchResult.Countries(countries)
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Ülkeler yüklenemedi"
                }
            )
            _isSearching.value = false
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query

        if (query.isEmpty()) {
            when (val step = _currentStep.value) {
                is SelectionStep.CountrySelection -> loadCountries()
                is SelectionStep.RegionSelection -> loadRegions(step.country)
                is SelectionStep.CitySelection -> loadCities(step.country, step.region)
            }
            return
        }

        if (query.length < 2) return

        when (val step = _currentStep.value) {
            is SelectionStep.CountrySelection -> searchCountries(query)
            is SelectionStep.RegionSelection -> searchRegions(step.country, query)
            is SelectionStep.CitySelection -> searchCities(step.country, step.region, query)
        }
    }

    private fun searchCountries(query: String) {
        viewModelScope.launch {
            _isSearching.value = true
            repository.searchCountries(query).fold(
                onSuccess = { countries ->
                    _searchResults.value = SearchResult.Countries(countries)
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )
            _isSearching.value = false
        }
    }

    private fun searchRegions(country: CountriesItem, query: String) {
        viewModelScope.launch {
            _isSearching.value = true
            repository.searchRegions(country.code, country.name, query).fold(
                onSuccess = { regions ->
                    _searchResults.value = SearchResult.Regions(regions)
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )
            _isSearching.value = false
        }
    }

    private fun searchCities(country: CountriesItem, region: String, query: String) {
        viewModelScope.launch {
            _isSearching.value = true
            repository.searchCities(country.code, country.name, region, query).fold(
                onSuccess = { cities ->
                    _searchResults.value = SearchResult.Cities(cities)
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )
            _isSearching.value = false
        }
    }

    fun onCountrySelected(country: CountriesItem) {
        _currentStep.value = SelectionStep.RegionSelection(country)
        _searchQuery.value = ""
        loadRegions(country)
    }

    private fun loadRegions(country: CountriesItem) {
        viewModelScope.launch {
            _isSearching.value = true
            repository.getRegions(country.code, country.name).fold(
                onSuccess = { regions ->
                    _searchResults.value = SearchResult.Regions(regions)
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Bölgeler yüklenemedi"
                }
            )
            _isSearching.value = false
        }
    }

    fun onRegionSelected(country: CountriesItem, region: String) {
        _currentStep.value = SelectionStep.CitySelection(country, region)
        _searchQuery.value = ""
        loadCities(country, region)
    }

    private fun loadCities(country: CountriesItem, region: String) {
        viewModelScope.launch {
            _isSearching.value = true
            repository.getCities(country.code, country.name, region).fold(
                onSuccess = { cities ->
                    _searchResults.value = SearchResult.Cities(cities)
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Şehirler yüklenemedi"
                }
            )
            _isSearching.value = false
        }
    }

    fun onCitySelected(country: CountriesItem, region: String, city: String) {
        viewModelScope.launch {
            _isSearching.value = true
            repository.addLocationByName(country.name, region, city).fold(
                onSuccess = { location ->
                    repository.setCurrentLocation(location.placeId)
                    _locationAdded.value = true
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Konum eklenemedi"
                }
            )
            _isSearching.value = false
        }
    }

    fun goBackStep() {
        when (val step = _currentStep.value) {
            is SelectionStep.RegionSelection -> {
                _currentStep.value = SelectionStep.CountrySelection
                _searchQuery.value = ""
                loadCountries()
            }
            is SelectionStep.CitySelection -> {
                _currentStep.value = SelectionStep.RegionSelection(step.country)
                _searchQuery.value = ""
                loadRegions(step.country)
            }
            else -> {}
        }
    }

    fun selectLocation(location: SavedLocation) {
        viewModelScope.launch {
            _loadingMessage.value = "Konum değiştiriliyor..."
            repository.setCurrentLocation(location.placeId)
            kotlinx.coroutines.delay(300)
            _loadingMessage.value = null
            _locationSelected.value = true
        }
    }

    fun resetLocationSelected() {
        _locationSelected.value = false
    }

    fun resetLocationAdded() {
        _locationAdded.value = false
    }

    fun toggleFavorite(placeId: Int) {
        viewModelScope.launch {
            repository.toggleFavorite(placeId)
        }
    }

    fun useGpsLocation() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            _needsLocationPermission.value = true
            return
        }

        _isLoadingGps.value = true
        viewModelScope.launch {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        viewModelScope.launch {
                            val result = repository.fetchAndSaveGpsLocation(location.latitude, location.longitude)
                            _isLoadingGps.value = false
                            if (result.isSuccess) {
                                _locationAdded.value = true
                            } else {
                                _errorMessage.value = result.exceptionOrNull()?.message ?: "Konum alınamadı"
                            }
                        }
                    } else {
                        _isLoadingGps.value = false
                        _errorMessage.value = "Konum bilgisi alınamadı. GPS açık mı?"
                    }
                }
            } catch (e: Exception) {
                _isLoadingGps.value = false
                _errorMessage.value = "GPS hatası: ${e.message}"
            }
        }
    }
}
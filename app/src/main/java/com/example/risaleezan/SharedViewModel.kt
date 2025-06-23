package com.example.risaleezan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// Veri sınıfına enlem ve boylam eklendi (nullable olarak)
data class SelectedLocation(
    val city: String,
    val country: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)

class SharedViewModel : ViewModel() {

    private val _selectedLocation = MutableLiveData<SelectedLocation>()
    val selectedLocation: LiveData<SelectedLocation> = _selectedLocation

    fun selectLocation(location: SelectedLocation) {
        // Eğer yeni konum mevcut konum ile aynıysa gereksiz güncelleme yapma
        if (_selectedLocation.value?.city == location.city && _selectedLocation.value?.country == location.country) {
            // Eğer koordinatlar yeni ekleniyorsa güncellemeye izin ver
            if (_selectedLocation.value?.latitude == null && location.latitude != null) {
                _selectedLocation.value = location
            }
            return
        }
        _selectedLocation.value = location
    }
}
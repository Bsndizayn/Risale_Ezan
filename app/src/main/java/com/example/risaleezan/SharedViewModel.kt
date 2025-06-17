package com.example.risaleezan // Paket adını kontrol et

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// Sadece şehir yerine, hem şehir hem ülke tutan bir veri sınıfı
data class SelectedLocation(val city: String, val country: String)

class SharedViewModel : ViewModel() {

    // LiveData'mız artık SelectedLocation nesnesi tutacak
    private val _selectedLocation = MutableLiveData<SelectedLocation>()
    val selectedLocation: LiveData<SelectedLocation> = _selectedLocation

    fun selectLocation(location: SelectedLocation) {
        _selectedLocation.value = location
    }
}
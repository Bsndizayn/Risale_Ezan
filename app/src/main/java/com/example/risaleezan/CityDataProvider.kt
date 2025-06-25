package com.example.risaleezan

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Locale

object CityDataProvider {

    private const val TAG = "CityDataProvider"
    private var allCities: List<CityInfo>? = null

    // Ülke kodlarını Türkçe isimlerine çevirmek için sabit bir liste
    private val countryNamesMap = mapOf(
        "TR" to "Türkiye", "DE" to "Almanya", "FR" to "Fransa", "GB" to "Birleşik Krallık",
        "SA" to "Suudi Arabistan", "US" to "Amerika Birleşik Devletleri", "NL" to "Hollanda",
        "AT" to "Avusturya", "BE" to "Belçika", "CH" to "İsviçre", "SE" to "İsveç",
        "NO" to "Norveç", "DK" to "Danimarka", "AU" to "Avustralya", "CA" to "Kanada"
    )

    suspend fun loadCities(context: Context) {
        if (allCities != null) return
        withContext(Dispatchers.IO) {
            val cityList = mutableListOf<CityInfo>()
            try {
                context.assets.open("cities15000.txt").use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).forEachLine { line ->
                        val parts = line.split("\t")
                        // Nüfusu 1000'den büyük olan şehirleri alıyoruz
                        if (parts.size > 14 && (parts[14].toIntOrNull() ?: 0) > 1000) {
                            try {
                                val name = parts[1]
                                val lat = parts[4].toDouble()
                                val lon = parts[5].toDouble()
                                val countryCode = parts[8]
                                if (name.isNotBlank() && countryCode.isNotBlank()) {
                                    cityList.add(CityInfo(name, countryCode, lat, lon))
                                }
                            } catch (e: Exception) { /* Hatalı satırı atla */ }
                        }
                    }
                }
                allCities = cityList
                Log.d(TAG, "${allCities?.size} şehir başarıyla yüklendi.")
            } catch (e: Exception) {
                Log.e(TAG, "Şehir veritabanı okunurken hata oluştu.", e)
            }
        }
    }

    fun getAllCountries(): List<Pair<String, String>> {
        if (allCities == null) return emptyList()

        // Veritabanındaki tüm farklı ülke kodlarını al
        val uniqueCountryCodes = allCities!!.map { it.countryCode }.toSet()

        // Bu kodları Türkçe isimleriyle eşleştirip listeyi oluştur
        return uniqueCountryCodes.mapNotNull { code ->
            countryNamesMap[code]?.let { name ->
                name to code // (Örn: "Türkiye" to "TR")
            }
        }.sortedBy { it.first } // Alfabetik olarak sırala
    }

    fun getCitiesByCountry(countryCode: String): List<CityInfo> {
        return allCities?.filter { it.countryCode.equals(countryCode, ignoreCase = true) } ?: emptyList()
    }
}
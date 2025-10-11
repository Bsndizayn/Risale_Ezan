package com.example.risaleezanvakticompose.data.remote


import com.example.risaleezanvakticompose.data.model.Coordinates
import com.example.risaleezanvakticompose.data.model.CountriesItem
import com.example.risaleezanvakticompose.data.model.Place
import com.example.risaleezanvakticompose.data.model.PrayerTimesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Vakit API'si için Retrofit interface tanımları
 * Base URL: https://vakit.vercel.app/
 */
interface ApiService {

    /**
     * Tüm ülkelerin listesini getirir
     * Endpoint: GET /api/countries
     *
     * @return Ülke listesi (Country objelerinden oluşan liste)
     */
    @GET("api/countries")
    suspend fun getCountries(): Response<List<CountriesItem>>

    /**
     * Belirtilen ülkeye ait bölgelerin/illerin listesini getirir
     * Endpoint: GET /api/regions?country=Turkey
     *
     * @param country Ülke adı (örn: "Turkey", "Germany")
     * @return Bölge isimleri listesi
     */
    @GET("api/regions")
    suspend fun getRegions(@Query("country") country: String): Response<List<String>>

    /**
     * Belirtilen ülke ve bölgeye ait şehirlerin/ilçelerin listesini getirir
     * Endpoint: GET /api/cities?country=Turkey&region=Istanbul
     *
     * @param country Ülke adı
     * @param region Bölge adı
     * @return Şehir isimleri listesi
     */
    @GET("api/cities")
    suspend fun getCities(
        @Query("country") country: String,
        @Query("region") region: String
    ): Response<List<String>>

    /**
     * GPS koordinatlarına yakın konumları getirir
     * Endpoint: GET /api/nearByPlaces?lat=40.0006929&lng=32.8519762&lang=tr
     *
     * @param lat Enlem koordinatı
     * @param lng Boylam koordinatı
     * @param lang Dil tercihi (varsayılan: "tr")
     * @return Yakındaki yerler listesi (Place objelerinden oluşan)
     */
    @GET("api/nearByPlaces")
    suspend fun getNearbyPlaces(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("lang") lang: String = "tr"
    ): Response<List<Place>>

    /**
     * Ülke, bölge ve şehir bilgisinden koordinatları getirir
     * Endpoint: GET /api/coordinates?country=Turkey&region=Ankara&city=Ankara
     *
     * Bu endpoint manuel konum seçiminde koordinat bilgisi almak için kullanılıyor
     *
     * @param country Ülke adı
     * @param region Bölge adı
     * @param city Şehir adı
     * @return Koordinat bilgisi içeren CoordinatesResponse objesi
     */
    @GET("api/coordinates")
    suspend fun getCoordinates(
        @Query("country") country: String,
        @Query("region") region: String,
        @Query("city") city: String
    ): Response<Coordinates>

    /**
     * GPS koordinatlarına göre namaz vakitlerini getirir
     * Endpoint: GET /api/timesForGPS?lat=39.91987&lng=32.85427&date=2023-10-29&days=1&timezoneOffset=180&calculationMethod=Turkey&lang=tr
     *
     * Bu endpoint kaydedilmiş koordinatlara göre namaz vakitlerini almak için kullanılıyor
     *
     * @param lat Enlem koordinatı
     * @param lng Boylam koordinatı
     * @param date Tarih (YYYY-MM-DD formatında, örn: "2025-10-05")
     * @param days Kaç günlük veri isteniyor (varsayılan: 1)
     * @param timezoneOffset Saat dilimi farkı dakika cinsinden (Türkiye için 180 = +3 saat)
     * @param calculationMethod Hesaplama yöntemi (Türkiye için "Turkey")
     * @param lang Dil tercihi (varsayılan: "tr")
     * @return Namaz vakitleri bilgisi içeren PrayerTimesResponse objesi
     */
    @GET("api/timesForGPS")
    suspend fun getPrayerTimesForGPS(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("date") date: String,
        @Query("days") days: Int = 1,
        @Query("timezoneOffset") timezoneOffset: Int = 180,
        @Query("calculationMethod") calculationMethod: String = "Turkey",
        @Query("lang") lang: String = "tr"
    ): Response<PrayerTimesResponse>
}
package com.example.risaleezanvakticompose.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.risaleezanvakticompose.data.local.entities.SavedLocation
import kotlinx.coroutines.flow.Flow

/**
 * SavedLocation tablosu için Data Access Object.
 *
 * DAO nedir? Database ile kodumuzu birbirinden ayıran bir abstraction layer'dır.
 * SQL sorgularını burada tanımlıyoruz ve Room bizim için bu SQL'leri çalıştıran
 * kodu otomatik generate ediyor.
 *
 * Neden DAO kullanıyoruz?
 * 1. TYPE SAFETY: SQL string'leri yerine Kotlin fonksiyonları kullanıyoruz,
 *    compile-time'da hata yakalıyoruz.
 * 2. TESTABILITY: Mock DAO oluşturup unit test yazabiliriz.
 * 3. SEPARATION OF CONCERNS: Database logic'i UI'dan tamamen ayrı.
 * 4. READABILITY: saveLocation() yazmak SQL yazmaktan çok daha okunabilir.
 */
@Dao
interface SavedLocationDao {

    /**
     * Yeni bir konum kaydeder veya mevcutu günceller.
     *
     * @Insert annotation'ı Room'a "bu fonksiyon INSERT SQL'i çalıştırsın" diyor.
     *
     * OnConflictStrategy.REPLACE ne demek?
     * Eğer aynı primary key'e sahip bir kayıt varsa (aynı placeId), eski kaydı
     * sil ve yenisini ekle. Bu sayede aynı konumu iki kez kaydetme sorunu olmuyor.
     *
     * Örnek senaryo:
     * 1. Kullanıcı GPS'den Ankara, Çankaya'yı kaydetti (placeId: 311055)
     * 2. Sonra manuel olarak tekrar Ankara, Çankaya'yı seçti
     * 3. REPLACE stratejisi sayesinde duplicate oluşmaz, sadece güncellenir
     *
     * Neden suspend function?
     * Database işlemleri yavaştır (disk I/O). Main thread'i bloke etmemek için
     * coroutine içinde çalıştırıyoruz. suspend keyword sayesinde bu fonksiyonu
     * sadece coroutine içinden çağırabiliriz, main thread'den çağrılamaz.
     *
     * @param location Kaydedilecek konum
     * @return Eklenen kaydın ID'si (auto-generated olmadığı için placeId döner)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: SavedLocation): Long

    /**
     * Birden fazla konumu aynı anda kaydeder.
     *
     * Vararg (variable arguments) kullanıyoruz, yani istediğimiz kadar parametre
     * geçebiliriz:
     * insertAll(location1)
     * insertAll(location1, location2, location3)
     *
     * Bu fonksiyon özellikle ilk kurulumda veya senkronizasyonda kullanışlı.
     * Örneğin API'den 10 popüler şehrin listesini çekip hepsini birden kaydedebiliriz.
     *
     * REPLACE stratejisi burada da var, yani mevcut kayıtlar güncellenecek.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg locations: SavedLocation)

    /**
     * Mevcut bir konumu günceller.
     *
     * @Update annotation'ı primary key'e bakarak hangi kaydı güncelleyeceğini buluyor.
     * Eğer placeId = 311055 olan bir SavedLocation geçersek, database'de placeId = 311055
     * olan kaydı bulup güncelliyor.
     *
     * Insert'ten farkı: Kayıt yoksa hata verir, yeni kayıt oluşturmaz.
     *
     * Kullanım senaryoları:
     * - Kullanıcı bir konumu favorite yaptı: location.copy(isFavorite = true).update()
     * - Kullanıcı başka konuma geçti: eskiKonum.copy(isCurrentLocation = false).update()
     *
     * @param location Güncellenecek konum (primary key ile eşleşen kayıt bulunur)
     */
    @Update
    suspend fun update(location: SavedLocation)

    /**
     * Bir konumu siler.
     *
     * Primary key'e bakarak hangi kaydı sileceğini buluyor.
     *
     * Önemli: Foreign key cascade tanımladığımız için, bir konum silindiğinde
     * o konuma ait tüm PrayerTimesEntity kayıtları da otomatik silinir.
     *
     * @param location Silinecek konum
     */
    @Delete
    suspend fun delete(location: SavedLocation)

    /**
     * Tüm kayıtlı konumları getirir.
     *
     * @Query annotation'ı ile custom SQL sorgusu yazıyoruz.
     *
     * Flow<List<SavedLocation>> dönüyoruz, suspend function değil!
     *
     * Flow nedir? Reactive programming'in Kotlin versiyonu. Flow, değerler stream'i
     * yayınlar ve bu stream'i collect ederek değişiklikleri gözlemleriz.
     *
     * Neden Flow kullanıyoruz?
     * Database'de bir değişiklik olduğunda (insert, update, delete) Flow otomatik
     * olarak yeni veriyi yayınlar. UI bu değişikliği anında görür.
     *
     * Örnek senaryo:
     * UI'da konumlar listesi gösteriyoruz. Kullanıcı yeni konum ekledi.
     * Flow sayesinde liste otomatik güncellenir, manuel refresh gerekmez.
     *
     * SELECT * FROM saved_locations sorgusu tüm kolonları, tüm satırları getirir.
     *
     * @return Tüm konumların Flow stream'i
     */
    @Query("SELECT * FROM saved_locations")
    fun getAllLocations(): Flow<List<SavedLocation>>

    /**
     * Belirli bir placeId'ye sahip konumu getirir.
     *
     * :placeId syntax'ı parametre binding. SQL injection'ı önler.
     *
     * Nullable dönüyor çünkü böyle bir ID olmayabilir.
     *
     * Suspend function çünkü tek seferlik sorgu, Flow'a gerek yok.
     *
     * @param placeId Aranacak konum ID'si
     * @return Konum bulunursa SavedLocation, yoksa null
     */
    @Query("SELECT * FROM saved_locations WHERE place_id = :placeId")
    suspend fun getLocationById(placeId: Int): SavedLocation?

    /**
     * Kullanıcının şu anki GPS konumunu getirir.
     *
     * isCurrentLocation = true olan tek bir kayıt olmalı sistemde.
     * Bu kurala code level'da dikkat edeceğiz.
     *
     * LIMIT 1 koyuyoruz güvenlik için. Eğer hatalı bir durumda birden fazla
     * kayıt true olmuşsa bile sadece ilkini alıyoruz.
     *
     * Flow kullanıyoruz çünkü GPS konumu değiştiğinde UI'ı güncellemek istiyoruz.
     *
     * @return Şu anki GPS konumu (varsa)
     */
    @Query("SELECT * FROM saved_locations WHERE is_current_location = 1 LIMIT 1")
    fun getCurrentLocation(): Flow<SavedLocation?>

    /**
     * Tüm favori konumları getirir.
     *
     * WHERE is_favorite = 1 koşulu true olanları filtreler.
     * SQLite'ta boolean değerler 0 (false) ve 1 (true) olarak tutulur.
     *
     * ORDER BY last_updated DESC: En son güncellenen favoriler en üstte.
     * Bu sayede kullanıcının en çok kullandığı konumlar listenin başında olur.
     *
     * @return Favori konumların Flow stream'i (en son kullanılandan başlayarak)
     */
    @Query("SELECT * FROM saved_locations WHERE is_favorite = 1 ORDER BY last_updated DESC")
    fun getFavoriteLocations(): Flow<List<SavedLocation>>

    /**
     * En son kullanılan konumu getirir (GPS konumu hariç).
     *
     * is_current_location = 0: GPS konumunu hariç tutuyoruz çünkü GPS konumu
     * her zaman "current" olarak işaretli. Biz manuel seçilen konumlardan
     * en son kullanılanı istiyoruz.
     *
     * ORDER BY last_updated DESC: En yeni timestamp en üstte.
     * LIMIT 1: Sadece ilk kaydı al.
     *
     * Kullanım senaryosu:
     * Kullanıcı uygulamayı kapattı, tekrar açtı. GPS'siz devam etmek istiyor.
     * En son hangi şehri kullanmıştı? Bu sorgu ile buluyoruz.
     *
     * @return En son kullanılan manuel konum
     */
    @Query("SELECT * FROM saved_locations WHERE is_current_location = 0 ORDER BY last_updated DESC LIMIT 1")
    suspend fun getLastUsedLocation(): SavedLocation?

    /**
     * Konum adına göre arama yapar (case-insensitive).
     *
     * LIKE operatörü pattern matching için kullanılır.
     * '%' wildcard: herhangi bir karakter dizisi anlamına gelir.
     *
     * Örnek:
     * searchQuery = "anka"
     * SQL: WHERE LOWER(place_name) LIKE '%anka%'
     *
     * Bulunacak yerler:
     * - "Ankara" (içinde "anka" var)
     * - "Çankaya" (içinde "anka" var)
     * - "Çankırı" (içinde "ankı" yok ama Türkçe 'ı' 'i' dönüşümü yapılmadı)
     *
     * LOWER() fonksiyonu hem kolon hem parametre için kullanılıyor.
     * Bu sayede "ANKARA", "ankara", "Ankara" hepsi eşleşir.
     *
     * '%' || :searchQuery || '%' string concatenation yapıyor.
     * searchQuery = "anka" ise '%anka%' oluyor.
     *
     * @param searchQuery Aranacak metin
     * @return Eşleşen konumların Flow stream'i
     */
    @Query("SELECT * FROM saved_locations WHERE LOWER(place_name) LIKE '%' || LOWER(:searchQuery) || '%'")
    fun searchLocations(searchQuery: String): Flow<List<SavedLocation>>

    /**
     * Tüm konumları siler.
     *
     * DELETE FROM table_name: Tablodaki tüm satırları siler.
     * WHERE koşulu yok, yani hepsi silinir.
     *
     * Dikkat: Bu tehlikeli bir operasyon! Kullanırken emin olmalıyız.
     * Genellikle sadece test veya reset senaryolarında kullanılır.
     *
     * Production'da kullanıcıya "Tüm konumları silmek istediğinizden emin misiniz?"
     * diye onay aldıktan sonra çağırmalıyız.
     */
    @Query("DELETE FROM saved_locations")
    suspend fun deleteAllLocations()

    /**
     * GPS konumu olarak işaretlenmiş tüm konumların flag'ini false yapar.
     *
     * @Transaction annotation'ı önemli! Bu işlem atomik olmalı.
     *
     * Atomik ne demek? "Ya hepsi başarılı olur ya hiçbiri" demek.
     * Örneğin 5 kayıt varsa ve 3. kayıtta hata olursa, ilk 2 kayıt da geri alınır (rollback).
     *
     * Neden bu fonksiyona ihtiyacımız var?
     * Yeni GPS konumu kaydetmeden önce eski GPS flag'lerini temizlememiz gerekiyor.
     * Çünkü sistemde sadece bir konum isCurrentLocation = true olabilir.
     *
     * UPDATE sorgusu:
     * SET is_current_location = 0: Tüm kayıtları false yap
     * WHERE is_current_location = 1: Sadece true olanları bul
     *
     * Bu WHERE koşulu performans için önemli. Zaten false olanları update etmemize gerek yok.
     *
     * Kullanım:
     * clearCurrentLocationFlags()  // Önce hepsini temizle
     * insert(newGpsLocation.copy(isCurrentLocation = true))  // Sonra yeniyi ekle
     */
    @Transaction
    @Query("UPDATE saved_locations SET is_current_location = 0 WHERE is_current_location = 1")
    suspend fun clearCurrentLocationFlags()

    /**
     * Bir konumun favorite durumunu toggle eder (açıp kapatır).
     *
     * CASE WHEN yapısı SQL'de if-else gibi çalışır.
     *
     * Okunabilir hali:
     * if (is_favorite == 1) {
     *     set is_favorite = 0
     * } else {
     *     set is_favorite = 1
     * }
     *
     * Neden böyle bir fonksiyon?
     * UI'da star icon'a tıklanınca favoriye ekle/çıkar yapıyoruz.
     * Bu fonksiyon sayesinde önce getir, check et, sonra set et yerine
     * tek sorguda toggle edebiliyoruz.
     *
     * Ayrıca last_updated'i günceliyoruz ki bu konumun "kullanıldığını" anlayalım.
     *
     * @param placeId Toggle edilecek konumun ID'si
     */
    @Query("""
        UPDATE saved_locations 
        SET is_favorite = CASE 
            WHEN is_favorite = 1 THEN 0 
            ELSE 1 
        END,
        last_updated = :timestamp
        WHERE place_id = :placeId
    """)
    suspend fun toggleFavorite(placeId: Int, timestamp: Long = System.currentTimeMillis())

    /**
     * Bir konumun lastUpdated timestamp'ini günceller.
     *
     * Bu fonksiyon kullanıcı bir konuma geçiş yaptığında çağrılır.
     * "Bu konumu şimdi kullandım" bilgisini kaydediyoruz.
     *
     * Böylece "en son kullanılan konum" sorgularında doğru sonuç gelir.
     *
     * @param placeId Timestamp'i güncellenecek konumun ID'si
     * @param timestamp Yeni timestamp değeri (default: şu anki zaman)
     */
    @Query("UPDATE saved_locations SET last_updated = :timestamp WHERE place_id = :placeId")
    suspend fun updateLastUsed(placeId: Int, timestamp: Long = System.currentTimeMillis())
}
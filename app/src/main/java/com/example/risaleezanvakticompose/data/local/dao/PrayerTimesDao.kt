package com.example.risaleezanvakticompose.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.risaleezanvakticompose.data.local.entities.PrayerTimesEntity
import kotlinx.coroutines.flow.Flow

/**
 * PrayerTimesEntity tablosu için Data Access Object.
 *
 * Bu DAO namaz vakitleri ile ilgili tüm database işlemlerini yönetiyor.
 * Tasarımda dikkat ettiğimiz noktalar:
 *
 * 1. EFFICIENT QUERIES: Gereksiz veri çekmiyoruz, WHERE ve LIMIT kullanıyoruz
 * 2. DATE RANGE SUPPORT: Gelecek N günün vakitlerini çekebiliyoruz
 * 3. CACHE MANAGEMENT: Eski vakitleri temizleme fonksiyonları var
 * 4. FLOW USAGE: UI otomatik güncellensin diye reactive queries kullanıyoruz
 */
@Dao
interface PrayerTimesDao {

    /**
     * Yeni namaz vakti kaydı ekler veya mevcutu günceller.
     *
     * REPLACE stratejisi kritik burada! Çünkü:
     * - Kullanıcı aynı konum için aynı tarihin vakitlerini tekrar çekebilir
     * - Vakitler değişmez normalde ama calculation method değişirse değişebilir
     * - Duplicate kayıt yerine update yapmak daha mantıklı
     *
     * Örnek senaryo:
     * 1. Bugünün vakitleri çekildi ve kaydedildi
     * 2. Kullanıcı "vakitleri yenile" butonuna bastı
     * 3. API'den tekrar çekildi
     * 4. REPLACE sayesinde duplicate oluşmadı, sadece update oldu
     *
     * @param prayerTimes Kaydedilecek namaz vakitleri
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prayerTimes: PrayerTimesEntity)

    /**
     * Birden fazla namaz vakti kaydını aynı anda ekler.
     *
     * Bu fonksiyon özellikle API'den toplu veri çekerken kullanışlı.
     * API bize genellikle 7 veya 30 günlük veriyi birden veriyor.
     *
     * Örnek:
     * API response: {"2023-10-29": [...], "2023-10-30": [...], ...}
     * Bu 7 günlük veriyi 7 ayrı PrayerTimesEntity'ye çevirip
     * insertAll() ile tek seferde kaydediyoruz.
     *
     * REPLACE stratejisi burada da var, mevcut kayıtlar güncellenecek.
     *
     * Neden tek tek insert yerine insertAll?
     * - Performans: Room single transaction içinde hepsini ekler, çok hızlı
     * - Atomicity: Ya hepsi başarılı olur ya hiçbiri (transaction garantisi)
     *
     * @param prayerTimes Kaydedilecek namaz vakitleri dizisi
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg prayerTimes: PrayerTimesEntity)

    /**
     * Belirli bir konumun belirli bir tarihteki namaz vakitlerini getirir.
     *
     * Bu fonksiyon en sık kullanılan sorgu. Örneğin ana ekranda
     * "Bugünün vakitleri nedir?" sorusunu cevaplıyor.
     *
     * WHERE koşulları:
     * - location_place_id = :placeId: Hangi konum?
     * - AND date = :date: Hangi tarih?
     *
     * LIMIT 1: Güvenlik önlemi. Normalde zaten tek kayıt olmalı çünkü
     * (placeId, date) kombinasyonu unique olmalı. Ama garanti için LIMIT koyuyoruz.
     *
     * Nullable döndürüyoruz çünkü:
     * - Bu tarih için veri henüz çekilmemiş olabilir
     * - Database boş olabilir
     * - Network hatası olmuş ve cache yoksa null gelir
     *
     * Suspend function çünkü tek seferlik sorgu, Flow'a gerek yok.
     *
     * @param placeId Konumun ID'si
     * @param date ISO formatında tarih string'i ("2023-10-29")
     * @return Namaz vakitleri varsa PrayerTimesEntity, yoksa null
     */
    @Query("SELECT * FROM prayer_times WHERE location_place_id = :placeId AND date = :date LIMIT 1")
    suspend fun getPrayerTimesForDate(placeId: Int, date: String): PrayerTimesEntity?

    /**
     * Belirli bir konumun belirli tarih aralığındaki tüm namaz vakitlerini getirir.
     *
     * BETWEEN operatörü iki tarih arasındaki kayıtları getirir (inclusive).
     *
     * Örnek:
     * startDate = "2023-10-29"
     * endDate = "2023-11-05"
     *
     * Bu sorgu 29 Ekim ile 5 Kasım arasındaki (ikisi de dahil) tüm günlerin
     * vakitlerini getirir. Toplamda 8 gün.
     *
     * ORDER BY date ASC: Tarihe göre sıralama (eskiden yeniye).
     * Böylece liste kronolojik sırada gelir.
     *
     * Flow kullanıyoruz çünkü:
     * - Bu tarih aralığında yeni veri eklenirse UI'ı güncellemek istiyoruz
     * - Kullanıcı haftalık view'de olabilir, her gün otomatik güncellensin
     *
     * Kullanım senaryosu:
     * Haftalık takvim gösteriyoruz. Önümüzdeki 7 günün vakitlerini
     * bu fonksiyonla çekip liste halinde gösteriyoruz.
     *
     * @param placeId Konumun ID'si
     * @param startDate Başlangıç tarihi (dahil)
     * @param endDate Bitiş tarihi (dahil)
     * @return Tarih aralığındaki tüm namaz vakitleri Flow stream'i
     */
    @Query("""
        SELECT * FROM prayer_times 
        WHERE location_place_id = :placeId 
        AND date BETWEEN :startDate AND :endDate 
        ORDER BY date ASC
    """)
    fun getPrayerTimesRange(placeId: Int, startDate: String, endDate: String): Flow<List<PrayerTimesEntity>>

    /**
     * Belirli bir konumun tüm kayıtlı namaz vakitlerini getirir.
     *
     * WHERE sadece placeId'ye bakıyor, tarih koşulu yok.
     * Yani bu konum için database'de ne varsa hepsini getiriyor.
     *
     * ORDER BY date ASC: Kronolojik sırada (en eski tarih ilk sırada).
     *
     * Bu fonksiyon genellikle debug veya admin amaçlı kullanılır.
     * Production'da belli tarih aralığı sorgulamak daha mantıklı.
     *
     * Örnek kullanım:
     * "Ankara için database'de toplam kaç günlük veri var?"
     * Bu sorguyu çağırıp liste boyutuna bakabiliriz.
     *
     * @param placeId Konumun ID'si
     * @return Tüm namaz vakitleri Flow stream'i (tarih sırasına göre)
     */
    @Query("SELECT * FROM prayer_times WHERE location_place_id = :placeId ORDER BY date ASC")
    fun getAllPrayerTimesForLocation(placeId: Int): Flow<List<PrayerTimesEntity>>

    /**
     * Belirli bir tarihten önceki tüm eski kayıtları siler.
     *
     * Cache temizliği için kullanılıyor. Geçmişte kalan vakitleri
     * tutmanın anlamı yok, yer kaplıyor sadece.
     *
     * WHERE date < :beforeDate: Bu tarihten önceki (excluding) kayıtlar.
     *
     * Örnek senaryo:
     * Bugün 10 Kasım. 1 Kasım'dan önceki tüm vakitleri silelim:
     * deleteOldPrayerTimes("2023-11-01")
     *
     * Bu sorgu 31 Ekim ve öncesindeki tüm kayıtları siler.
     * 1 Kasım ve sonrası kalır.
     *
     * Ne zaman çağrılmalı?
     * - Uygulama her açıldığında background'da çalıştırılabilir
     * - WorkManager ile günlük olarak schedule edilebilir
     * - Ayarlar ekranından "cache temizle" butonu ile manuel
     *
     * @param beforeDate Bu tarihten önceki kayıtlar silinecek (ISO format)
     */
    @Query("DELETE FROM prayer_times WHERE date < :beforeDate")
    suspend fun deleteOldPrayerTimes(beforeDate: String)

    /**
     * Belirli bir konumun tüm namaz vakitlerini siler.
     *
     * Kullanıcı bir konumu sildiğinde o konumun vakitlerini de silmeliyiz.
     *
     * Ama dikkat! SavedLocation tablosunda Foreign Key CASCADE tanımladık.
     * Yani SavedLocation silindiğinde PrayerTimes otomatik silinir.
     *
     * Peki bu fonksiyon ne işe yarıyor?
     * Kullanıcı konumu silmeden sadece o konumun vakitlerini yenilemek isteyebilir:
     * 1. Eski vakitleri sil: deletePrayerTimesForLocation(placeId)
     * 2. API'den yeni vakitleri çek
     * 3. Yeni vakitleri kaydet: insertAll(...)
     *
     * Bu "hard refresh" senaryosu için kullanışlı.
     *
     * @param placeId Vakitleri silinecek konumun ID'si
     */
    @Query("DELETE FROM prayer_times WHERE location_place_id = :placeId")
    suspend fun deletePrayerTimesForLocation(placeId: Int)

    /**
     * Tüm namaz vakti kayıtlarını siler.
     *
     * Tehlikeli operasyon! Tüm cache'i uçuruyor.
     *
     * Ne zaman kullanılır?
     * - Test senaryolarında
     * - Kullanıcı "tüm cache'i temizle" dediğinde
     * - Calculation method değiştiğinde (tüm vakitler yeniden hesaplanmalı)
     * - Factory reset özelliği
     *
     * Production'da dikkatli kullanılmalı, kullanıcıya onay sorulmalı.
     */
    @Query("DELETE FROM prayer_times")
    suspend fun deleteAllPrayerTimes()

    /**
     * Database'de kaç kayıt olduğunu sayar.
     *
     * COUNT(*) SQL'in aggregate fonksiyonu, tüm satırları sayar.
     *
     * Bu bilgiyi nerede kullanabiliriz?
     * - Ayarlar ekranında: "Cache boyutu: 245 günlük veri"
     * - Debug panel'de: "Toplam kayıt sayısı: 1250"
     * - Analytics'de: Kullanıcılar ne kadar veri biriktiriyor?
     * - Cache limiti: 1000 kayıt geçerse eski kayıtları temizle
     *
     * @return Database'deki toplam kayıt sayısı
     */
    @Query("SELECT COUNT(*) FROM prayer_times")
    suspend fun getPrayerTimesCount(): Int

    /**
     * Belirli bir konumun en eski ve en yeni vakitlerinin tarihlerini getirir.
     *
     * Bu fonksiyon biraz karmaşık, açıklayalım:
     *
     * MIN(date): En küçük tarih değeri = En eski kayıt
     * MAX(date): En büyük tarih değeri = En yeni kayıt
     *
     * Örnek sonuç:
     * DateRange(minDate = "2023-10-29", maxDate = "2023-11-05")
     *
     * Bu "Ankara için 29 Ekim'den 5 Kasım'a kadar veri var" demek.
     *
     * Nullable çünkü hiç kayıt yoksa null gelir.
     *
     * Nerede kullanılır?
     * - "X gün önce - Y gün sonrasına kadar veri var" mesajı göstermek için
     * - Eksik günleri bulmak için: Bugün 1 Kasım, ama maxDate 30 Ekim.
     *   Demek ki yeni veri çekmemiz gerekiyor.
     * - Cache freshness kontrolü
     *
     * @param placeId Konumun ID'si
     * @return En eski ve en yeni tarihler (varsa)
     */
    @Query("SELECT MIN(date) as minDate, MAX(date) as maxDate FROM prayer_times WHERE location_place_id = :placeId")
    suspend fun getDateRangeForLocation(placeId: Int): DateRange?

    /**
     * Belirli bir tarihten sonraki vakitlerin olup olmadığını kontrol eder.
     *
     * EXISTS SQL keyword'ü varsa true, yoksa false döner.
     *
     * Bu query çok performanslı çünkü sadece existence check yapıyor,
     * veriyi getirmiyor. İlk kayıt bulunduğunda hemen true dönüp duruyor.
     *
     * Ne işe yarıyor?
     * Önümüzdeki günler için veri var mı kontrol etmek için.
     *
     * Kullanım:
     * val hasNextWeekData = hasPrayerTimesAfterDate(placeId, bugununTarihi)
     * if (!hasNextWeekData) {
     *     // API'den gelecek hafta verilerini çek
     * }
     *
     * @param placeId Konumun ID'si
     * @param afterDate Bu tarihten sonrası kontrol edilecek
     * @return Veri varsa true, yoksa false
     */
    @Query("SELECT EXISTS(SELECT 1 FROM prayer_times WHERE location_place_id = :placeId AND date > :afterDate)")
    suspend fun hasPrayerTimesAfterDate(placeId: Int, afterDate: String): Boolean

    /**
     * Birden fazla konumun belirli bir tarihteki vakitlerini tek sorguda getirir.
     *
     * IN operatörü liste içinde arama yapar.
     *
     * Örnek:
     * placeIds = listOf(311055, 311046, 311048)  // Çankaya, Keçiören, Mamak
     * date = "2023-10-29"
     *
     * Bu sorgu bu 3 ilçenin 29 Ekim vakitlerini tek seferde getirir.
     *
     * Nerede kullanılır?
     * Widget'larda: Kullanıcı 3 farklı şehri widget'a eklemiş, hepsinin
     * bugünkü vakitlerini göstermek istiyoruz. Tek sorgu ile hepsini çekiyoruz.
     *
     * Comparison ekranı: "İstanbul vs Ankara bugünkü vakitler" karşılaştırması.
     *
     * @param placeIds Konum ID'leri listesi
     * @param date Tarih
     * @return Bu konumların bu tarihteki vakitleri
     */
    @Query("SELECT * FROM prayer_times WHERE location_place_id IN (:placeIds) AND date = :date")
    suspend fun getPrayerTimesForMultipleLocations(placeIds: List<Int>, date: String): List<PrayerTimesEntity>

    /**
     * Toplu silme ve ekleme işlemini tek transaction içinde yapar.
     *
     * @Transaction annotation'ı kritik! Bu iki işlemi atomik yapıyor.
     *
     * Ne işe yarıyor?
     * API'den vakitleri yeniden çektiğimizde, önce eskilerini silip
     * sonra yenilerini ekliyoruz. Bu işlem ortasında hata olursa
     * ne silme ne de ekleme gerçekleşir (rollback).
     *
     * Örnek senaryo:
     * 1. Kullanıcı calculation method'u "Turkey"den "MWL"ye değiştirdi
     * 2. Tüm vakitler yeniden hesaplanmalı
     * 3. Bu fonksiyonu çağırıyoruz:
     *    - Önce tüm eski kayıtları sil (Turkey hesaplı)
     *    - Sonra yeni kayıtları ekle (MWL hesaplı)
     * 4. İşlem ortasında crash olsa bile tutarsız durum oluşmaz
     *
     * Transaction güvenliği sayesinde:
     * - Ya hem silme hem ekleme başarılı olur (commit)
     * - Ya hiçbiri olmaz, eski hali kalır (rollback)
     * - Yarım yamalak durum asla olmaz
     *
     * @param prayerTimes Yeni eklenecek vakitler
     */
    @Transaction
    suspend fun deleteAllAndInsert(vararg prayerTimes: PrayerTimesEntity) {
        deleteAllPrayerTimes()
        insertAll(*prayerTimes)
    }
}

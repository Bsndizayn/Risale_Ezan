package com.example.risaleezanvakticompose.data.local

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * ════════════════════════════════════════════════════════════════════════════════
 * ROOM TYPE CONVERTERS - Özel Tipleri SQLite'a Çevirme
 * ════════════════════════════════════════════════════════════════════════════════
 *
 * TypeConverter nedir?
 * Room sadece primitive tipleri (Int, String, Boolean, Long, Double) ve bunların
 * nullable versiyonlarını doğrudan destekler. Diğer tipleri (Date, List, Custom class)
 * kullanmak için TypeConverter yazmamız gerekir.
 *
 * TypeConverter, bir tipi SQLite'ın anlayabileceği bir tipe çevirir ve geri dönüştürür.
 *
 * Örnek: LocalDate (Kotlin/Java tipi) → String (SQLite tipi) → LocalDate
 *
 *
 * Neden TypeConverter gerekli?
 * ───────────────────────────
 * SQLite çok basit bir veritabanıdır. Sadece 5 data type vardır:
 * 1. NULL
 * 2. INTEGER (Int, Long, Boolean)
 * 3. REAL (Double, Float)
 * 4. TEXT (String)
 * 5. BLOB (ByteArray)
 *
 * Modern Kotlin kodu LocalDate, LocalTime, Instant, custom data class'lar kullanır.
 * Bunları database'e kaydetmek için SQLite type'larına çevirmemiz lazım.
 *
 *
 * TypeConverter nasıl çalışır?
 * ────────────────────────────
 * İki yönlü dönüşüm fonksiyonları yazıyoruz:
 * - TO DATABASE: Kotlin type → SQLite type
 * - FROM DATABASE: SQLite type → Kotlin type
 *
 * @TypeConverter annotation'ı Room'a "bu fonksiyon type converter" diyor.
 * Room compile-time'da bu fonksiyonları bulur ve gerekli yerlerde otomatik çağırır.
 *
 *
 * Bu class'ta ne var?
 * ───────────────────
 * Şu anda kullanmadığımız ama gelecekte işimize yarayabilecek converter'lar var:
 *
 * 1. LocalDate Converters: Tarih objelerini String'e çevirme
 * 2. LocalTime Converters: Saat objelerini String'e çevirme
 * 3. LocalDateTime Converters: Tarih+saat objelerini String'e çevirme
 * 4. Instant/Timestamp Converters: Unix timestamp çevirme
 * 5. List<String> Converters: String listelerini JSON'a çevirme
 *
 * Şimdilik bunları kullanmıyoruz çünkü entity'lerimizde String kullanıyoruz.
 * Ama ileride refactor edip LocalDate, LocalTime kullanmak istersek hazır.
 *
 *
 * Neden şimdi yazmıyoruz?
 * ───────────────────────
 * YAGNI principle: "You Aren't Gonna Need It"
 * İhtiyaç olmayan kodu yazma. Ama buraya örnek olarak koyuyorum çünkü:
 * 1. Eğitici: TypeConverter nasıl yazılır görmüş oluyorsunuz
 * 2. Referans: İleride lazım olursa kopyala-yapıştır yapabilirsiniz
 * 3. Best practice: Hangi converter'ları yazmak gerekir gösteriyorum
 */
class Converters {

    /**
     * ════════════════════════════════════════════════════════════════════════════
     * LOCALDATE CONVERTERS - Tarih Dönüşümleri
     * ════════════════════════════════════════════════════════════════════════════
     *
     * LocalDate: Java 8+ ile gelen modern tarih API'si.
     * Sadece tarih tutar (gün, ay, yıl), saat tutmaz.
     *
     * Örnek: LocalDate.of(2023, 10, 29) → 29 Ekim 2023
     *
     * Neden LocalDate kullanmalı?
     * - Immutable: Thread-safe, değiştirilemez
     * - Type-safe: Tarih ve saat karışmaz
     * - Rich API: plusDays(), isBefore() gibi metodlar var
     * - Timezone sorunları yok: Sadece tarih, timezone ile işi yok
     *
     * Neden String yerine LocalDate?
     * String ile tarih tutmak hatalara açık:
     * - "2023-13-45" geçersiz ama String olarak kabul edilir
     * - Tarih karşılaştırma yapmak zor: "2023-10-29" > "2023-09-30" string comparison
     * - Tarih hesaplamaları manuel: 7 gün eklemek için kendin parse et, hesapla, format et
     *
     * LocalDate ile:
     * - Geçersiz tarih compile-time'da yakalanır
     * - date1.isAfter(date2) direkt çalışır
     * - date.plusDays(7) tek satır
     */

    /**
     * LocalDate → String (Database'e kaydetmek için)
     *
     * ISO 8601 formatını kullanıyoruz: "YYYY-MM-DD"
     * Örnek: 2023-10-29
     *
     * ISO 8601 neden iyi?
     * - International standard: Dünya genelinde kabul görmüş
     * - Sortable: String olarak sıralama doğru çalışır
     *   "2023-01-15" < "2023-12-31" (doğru)
     * - Compact: Kısa ve öz
     * - Unambiguous: Ay/gün karışıklığı yok (US: MM/DD, EU: DD/MM)
     *
     * Nullable parametre çünkü database'de NULL olabilir.
     * Null gelirse null dönüyoruz, dönüşüm yapmıyoruz.
     *
     * @param date Dönüştürülecek LocalDate (nullable)
     * @return ISO formatında string veya null
     */
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(DateTimeFormatter.ISO_LOCAL_DATE)
        // ISO_LOCAL_DATE = "yyyy-MM-dd" pattern'i
        // Örnek: LocalDate.of(2023, 10, 29) → "2023-10-29"
    }

    /**
     * String → LocalDate (Database'den okumak için)
     *
     * ISO formatındaki string'i LocalDate'e parse ediyoruz.
     *
     * LocalDate.parse() fonksiyonu ISO formatı default olarak kabul eder.
     * Formatter belirtmeye gerek yok, "2023-10-29" otomatik parse edilir.
     *
     * Hata senaryosu:
     * Eğer string geçersiz formatsa (örn: "29-10-2023") DateTimeParseException fırlatır.
     * Bu exception catch edilmezse app crash eder.
     *
     * Production'da exception handling eklenebilir:
     * return date?.let {
     *     try {
     *         LocalDate.parse(it)
     *     } catch (e: DateTimeParseException) {
     *         null  // veya default bir tarih döndür
     *     }
     * }
     *
     * @param date ISO formatında string (nullable)
     * @return Parse edilmiş LocalDate veya null
     */
    @TypeConverter
    fun toLocalDate(date: String?): LocalDate? {
        return date?.let { LocalDate.parse(it) }
        // Örnek: "2023-10-29" → LocalDate(2023, 10, 29)
    }

    /**
     * ════════════════════════════════════════════════════════════════════════════
     * LOCALTIME CONVERTERS - Saat Dönüşümleri
     * ════════════════════════════════════════════════════════════════════════════
     *
     * LocalTime: Sadece saat tutar (saat, dakika, saniye, nanosaniye).
     * Tarih tutmaz, timezone tutmaz.
     *
     * Örnek: LocalTime.of(14, 30) → 14:30 (öğleden sonra 2:30)
     *
     * Namaz vakitleri için ideal çünkü:
     * - Sadece saat bilgisi gerekli: "12:37"
     * - Tarih ayrı tutulur: date + time ayrımı
     * - Timezone gerekmez: Lokal saat
     *
     * String vs LocalTime karşılaştırması:
     * String: "12:37" → Sadece metin, matematik yapılamaz
     * LocalTime: LocalTime.of(12, 37) → time1.isBefore(time2) çalışır
     */

    /**
     * LocalTime → String
     *
     * ISO time formatı: "HH:mm:ss" veya "HH:mm"
     *
     * ISO_LOCAL_TIME formatter saniye ve nanosaniye de ekler eğer varsa:
     * - LocalTime.of(12, 37) → "12:37:00"
     * - LocalTime.of(12, 37, 30) → "12:37:30"
     * - LocalTime.of(12, 37, 30, 500000000) → "12:37:30.500"
     *
     * Namaz vakitleri için saniye gerekmez, sadece "HH:mm" yeterli.
     * İsterseniz custom formatter kullanabilirsiniz:
     * date?.format(DateTimeFormatter.ofPattern("HH:mm"))
     *
     * @param time Dönüştürülecek LocalTime (nullable)
     * @return ISO formatında saat string'i veya null
     */
    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.format(DateTimeFormatter.ISO_LOCAL_TIME)
        // Örnek: LocalTime.of(12, 37) → "12:37:00"
    }

    /**
     * String → LocalTime
     *
     * ISO formatındaki saat string'ini parse ediyor.
     *
     * Kabul edilen formatlar:
     * - "12:37" → LocalTime(12, 37, 0)
     * - "12:37:30" → LocalTime(12, 37, 30)
     * - "12:37:30.500" → LocalTime(12, 37, 30, 500000000)
     *
     * 24 saat formatı kullanılır. 12 saat formatı (AM/PM) desteklenmez.
     * "02:30 PM" geçersizdir, "14:30" olmalı.
     *
     * @param time ISO formatında saat string'i (nullable)
     * @return Parse edilmiş LocalTime veya null
     */
    @TypeConverter
    fun toLocalTime(time: String?): LocalTime? {
        return time?.let { LocalTime.parse(it) }
        // Örnek: "12:37" → LocalTime(12, 37)
    }

    /**
     * ════════════════════════════════════════════════════════════════════════════
     * LOCALDATETIME CONVERTERS - Tarih + Saat Dönüşümleri
     * ════════════════════════════════════════════════════════════════════════════
     *
     * LocalDateTime: Tarih + Saat kombine eder.
     * Örnek: 2023-10-29T14:30:00 (29 Ekim 2023, saat 14:30)
     *
     * Ne zaman kullanılır?
     * - Exact moment gerektiğinde: "Kullanıcı tam olarak ne zaman login oldu?"
     * - Log kayıtları: "Bu hata ne zaman oluştu?"
     * - Appointment'lar: "Randevu tarihi ve saati: 29 Ekim 14:30"
     *
     * LocalDate + LocalTime vs LocalDateTime:
     * - Ayrı tutmak: Flexibility var, tarih ve saati ayrı sorgulayabilirsin
     * - Birleştirmek: Simplicity var, tek field yeterli
     *
     * Bizim uygulamada kullanım yeri:
     * - fetchedAt timestamp'i: Veri ne zaman çekildi?
     * - lastUpdated: Kayıt ne zaman güncellendi?
     *
     * Ama biz Long (Unix timestamp) kullanıyoruz şu anda. LocalDateTime'a geçebiliriz.
     */

    /**
     * LocalDateTime → String
     *
     * ISO 8601 format: "yyyy-MM-dd'T'HH:mm:ss"
     *
     * 'T' karakteri tarih ve saat arasında ayırıcı olarak kullanılır.
     * Bu international standard'dır.
     *
     * Örnekler:
     * - 2023-10-29T14:30:00
     * - 2023-10-29T00:00:00 (gece yarısı)
     * - 2023-10-29T23:59:59 (günün sonu)
     *
     * Timezone bilgisi yok! LocalDateTime timezone-agnostic'tir.
     * Eğer timezone gerekirse ZonedDateTime kullanılmalı.
     *
     * @param dateTime Dönüştürülecek LocalDateTime (nullable)
     * @return ISO formatında string veya null
     */
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        // Örnek: LocalDateTime.of(2023, 10, 29, 14, 30) → "2023-10-29T14:30:00"
    }

    /**
     * String → LocalDateTime
     *
     * ISO formatındaki string'i parse ediyor.
     *
     * Kabul edilen formatlar:
     * - "2023-10-29T14:30"
     * - "2023-10-29T14:30:00"
     * - "2023-10-29T14:30:00.500"
     *
     * @param dateTime ISO formatında string (nullable)
     * @return Parse edilmiş LocalDateTime veya null
     */
    @TypeConverter
    fun toLocalDateTime(dateTime: String?): LocalDateTime? {
        return dateTime?.let { LocalDateTime.parse(it) }
        // Örnek: "2023-10-29T14:30:00" → LocalDateTime(2023, 10, 29, 14, 30, 0)
    }

    /**
     * ════════════════════════════════════════════════════════════════════════════
     * TIMESTAMP CONVERTERS - Unix Timestamp Dönüşümleri
     * ════════════════════════════════════════════════════════════════════════════
     *
     * Instant: Zaman çizgisinde bir nokta. UTC timezone'da.
     * Unix Epoch'tan (1 Ocak 1970, 00:00:00 UTC) bu yana geçen süre.
     *
     * Long (Unix timestamp) vs Instant:
     * - Long: Primitive tip, basit, hızlı
     * - Instant: Obje, rich API, daha okunabilir
     *
     * Long kullanmanın artıları:
     * + Primitive, memory efficient
     * + SQLite doğrudan destekler (INTEGER)
     * + Math kolay: timestamp1 - timestamp2 = fark (milliseconds)
     *
     * Instant kullanmanın artıları:
     * + Type-safe: Yanlışlıkla başka Long ile karıştırılmaz
     * + Rich API: instant.plus(5, ChronoUnit.DAYS)
     * + Timezone conversion kolay: instant.atZone(ZoneId.systemDefault())
     *
     * Şu anda Long kullanıyoruz (lastUpdated, fetchedAt).
     * İleride Instant'a geçmek istersek bu converter'lar hazır.
     */

    /**
     * Instant → Long (Unix timestamp in milliseconds)
     *
     * Instant.toEpochMilli(): 1970'ten bu yana geçen milliseconds.
     *
     * Örnek:
     * Instant.now() (29 Ekim 2023, 14:30:00 UTC)
     * → 1698588600000 (milliseconds)
     *
     * Neden milliseconds?
     * - Seconds yeterli değil mi? Çoğu zaman yeterli ama:
     * - Milliseconds daha precise: İki işlem arasında geçen süreyi ölçmek için
     * - Android standard: System.currentTimeMillis() millis döner
     * - Database timestamp'ler genelde millis kullanır
     *
     * @param instant Dönüştürülecek Instant (nullable)
     * @return Unix timestamp (milliseconds) veya null
     */
    @TypeConverter
    fun fromInstant(instant: Instant?): Long? {
        return instant?.toEpochMilli()
        // Örnek: Instant.now() → 1698588600000
    }

    /**
     * Long → Instant
     *
     * Instant.ofEpochMilli(): Milliseconds'dan Instant oluşturur.
     *
     * Örnek:
     * 1698588600000 (milliseconds)
     * → Instant (29 Ekim 2023, 14:30:00 UTC)
     *
     * UTC'de oluşturur! Local timezone'a çevirmek için:
     * instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
     *
     * @param timestamp Unix timestamp (milliseconds, nullable)
     * @return Instant veya null
     */
    @TypeConverter
    fun toInstant(timestamp: Long?): Instant? {
        return timestamp?.let { Instant.ofEpochMilli(it) }
        // Örnek: 1698588600000 → Instant(2023-10-29T14:30:00Z)
    }

    /**
     * ════════════════════════════════════════════════════════════════════════════
     * LIST<STRING> CONVERTERS - String Listesi Dönüşümleri
     * ════════════════════════════════════════════════════════════════════════════
     *
     * SQLite'ta list veya array tipi yok. List tutmak için iki yaklaşım var:
     *
     * 1. SEPARATE TABLE (Normalized):
     *    Parent-child relationship. Örneğin bir Location'ın birden fazla
     *    alternative name'i varsa ayrı tablo: alternative_names
     *    Artıları: Query kolaylığı, veri bütünlüğü
     *    Eksileri: Kompleks, JOIN gerekir
     *
     * 2. JSON STRING (Denormalized):
     *    List'i JSON string'e çevir, tek kolonda tut.
     *    Artıları: Basit, tek sorgu yeterli
     *    Eksileri: JSON parse maliyeti, query zorluğu
     *
     * Place entity'sinde alternativeNames var (List<String>).
     * Bu list'i sorgulamayacağız, sadece göstereceğiz. O yüzden JSON uygun.
     *
     * Eğer "alternativeNames'de 'Ankara' geçen yerleri bul" sorgusu yapmak
     * isteseydik, separate table kullanmalıydık.
     */

    /**
     * List<String> → String (JSON formatında)
     *
     * Liste elemanlarını virgülle birleştirip JSON array'e çeviriyoruz.
     *
     * Örnek:
     * listOf("Ankara", "Ancara", "Angora")
     * → "[\"Ankara\",\"Ancara\",\"Angora\"]"
     *
     * Neden basit join(",") değil de JSON?
     * join(",") sorunlu:
     * - Eğer eleman içinde virgül varsa problem: ["New York, NY"] → "New York, NY"
     *   Parse ederken iki eleman zannedilir
     * - Escape etmek gerekir: Kompleks
     *
     * JSON avantajları:
     * - Standard format, her yerde parse edilebilir
     * - Escape otomatik: "New, York" → "\"New, York\""
     * - Nested structure destekler (gerekirse)
     *
     * joinToString fonksiyonu:
     * - prefix: Başa eklenecek string → "["
     * - postfix: Sona eklenecek string → "]"
     * - separator: Elemanlar arası → ","
     * - transform: Her elemana uygulanacak dönüşüm → "\"$it\""
     *
     * transform = { "\"$it\"" } her elemanı tırnak içine alıyor.
     *
     * @param list String listesi (nullable)
     * @return JSON formatında string veya null
     */
    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return list?.joinToString(
            separator = ",",
            prefix = "[",
            postfix = "]",
            transform = { "\"$it\"" }
        )
        // Örnek: ["Ankara", "Angora"] → "[\"Ankara\",\"Angora\"]"
    }

    /**
     * String → List<String> (JSON'dan parse)
     *
     * JSON array string'ini parse edip List<String>'e çeviriyoruz.
     *
     * Manuel parsing yapıyoruz (library kullanmadan):
     * 1. Baştaki "[" ve sondaki "]"'yi kaldır
     * 2. Virgüllerle split et
     * 3. Her elemanın başındaki/sonundaki tırnakları ve whitespace'leri temizle
     * 4. Boş elemanları filtrele
     *
     * Neden JSON library kullanmıyoruz (Gson, kotlinx.serialization)?
     * - Basit bir liste için overkill
     * - Dependency eklemek gereksiz
     * - Manuel parsing hızlı ve yeterli
     *
     * Alternatif olarak Gson kullanılabilir:
     * return Gson().fromJson(string, object : TypeToken<List<String>>() {}.type)
     *
     * Ama bu converter için manuel yeterli çünkü:
     * - Sadece String list, nested object yok
     * - Parse basit: split + trim
     *
     * Kod açıklaması:
     * - removeSurrounding("[", "]"): "[\"a\"]" → "\"a\""
     * - split(","): "\"a\",\"b\"" → ["\"a\"", "\"b\""]
     * - map { it.trim().removeSurrounding("\"") }: Her elemandan tırnak ve boşluk at
     * - filter { it.isNotEmpty() }: Boş string'leri filtrele
     *
     * Edge case'ler:
     * - "[]" → emptyList()
     * - null veya boş string → null
     * - "[\"Ankara\"]" → listOf("Ankara")
     * - "[\"Ankara\",\"Angora\"]" → listOf("Ankara", "Angora")
     *
     * @param string JSON formatında string (nullable)
     * @return Parse edilmiş string listesi veya null
     */
    @TypeConverter
    fun toStringList(string: String?): List<String>? {
        return string
            ?.removeSurrounding("[", "]")  // Bracket'leri kaldır
            ?.split(",")                    // Virgüllerle böl
            ?.map { it.trim().removeSurrounding("\"") }  // Her elemanı temizle
            ?.filter { it.isNotEmpty() }   // Boş elemanları filtrele
        // Örnek: "[\"Ankara\",\"Angora\"]" → ["Ankara", "Angora"]
    }

    /**
     * ════════════════════════════════════════════════════════════════════════════
     * NASIL KULLANILIR?
     * ════════════════════════════════════════════════════════════════════════════
     *
     * Bu converter'ları AppDatabase'e register etmek gerekir:
     *
     * @Database(...)
     * @TypeConverters(Converters::class)  // ← Burada register ediyoruz
     * abstract class AppDatabase : RoomDatabase() {
     *     ...
     * }
     *
     * Bundan sonra entity'lerde bu tipleri kullanabiliriz:
     *
     * @Entity
     * data class Event(
     *     @PrimaryKey val id: Int,
     *     val date: LocalDate,           // ← TypeConverter otomatik çalışır
     *     val time: LocalTime,            // ← TypeConverter otomatik çalışır
     *     val tags: List<String>          // ← TypeConverter otomatik çalışır
     * )
     *
     * Room compile-time'da bu converter'ları bulur ve gerekli yerlerde otomatik çağırır.
     * Biz manuel olarak çağırmayız, Room halleder.
     *
     * Database'de nasıl tutulur?
     * - date kolonu: TEXT ("2023-10-29")
     * - time kolonu: TEXT ("14:30:00")
     * - tags kolonu: TEXT ("[\"tag1\",\"tag2\"]")
     *
     * Query yaparken:
     * val event = eventDao.getById(1)
     * // event.date otomatik LocalDate olarak gelir (String'den parse edilir)
     * // event.time otomatik LocalTime olarak gelir
     * // event.tags otomatik List<String> olarak gelir
     *
     * Insert yaparken:
     * val event = Event(1, LocalDate.now(), LocalTime.now(), listOf("important"))
     * eventDao.insert(event)
     * // date, time, tags otomatik String'e çevrilip database'e yazılır
     */
}
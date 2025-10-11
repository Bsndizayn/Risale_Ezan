package com.example.risaleezanvakticompose.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Belirli bir konum için belirli bir günün namaz vakitlerini tutan entity.
 *
 * Bu entity'nin tasarımında dikkat ettiğimiz noktalar:
 *
 * 1. FOREIGN KEY RELATIONSHIP:
 *    Her namaz vakti kaydı bir konuma aittir. locationPlaceId alanı
 *    SavedLocation tablosundaki placeId'ye referans verir.
 *
 *    onDelete = CASCADE: Eğer bir konum silinirse, o konuma ait tüm
 *    namaz vakti kayıtları otomatik silinir. Bu "orphan records"
 *    (sahipsiz kayıtlar) oluşmasını engeller.
 *
 * 2. COMPOSITE UNIQUENESS:
 *    Aynı konum için aynı tarihte birden fazla kayıt olmamalı.
 *    Bunu code level'da kontrol edeceğiz (INSERT OR REPLACE kullanarak).
 *
 * 3. INDEXLEME:
 *    locationPlaceId ve date kolonlarına index ekledik çünkü bu kolonlarda
 *    çok sık WHERE clause kullanacağız:
 *    - "Bu konumun bugünkü vakitleri nedir?"
 *    - "Bu tarihteki tüm konumların vakitleri nedir?"
 *    Index olmadan bu sorgular yavaş olurdu.
 *
 * 4. NORMALIZED DATA:
 *    Her bir namaz vaktini ayrı kolon olarak tutuyoruz. Alternatif olarak
 *    JSON veya List<String> şeklinde tek kolonda tutabilirdik ama bu
 *    yaklaşım daha iyi çünkü:
 *    - SQL sorguları daha kolay (WHERE imsak > '05:00' gibi)
 *    - Type safety var (String olduğunu compile-time'da biliyoruz)
 *    - Her vakti ayrı ayrı update edebiliriz (gerekirse)
 */
@Entity(
    tableName = "prayer_times",
    foreignKeys = [
        ForeignKey(
            entity = SavedLocation::class,
            parentColumns = ["place_id"],
            childColumns = ["location_place_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["location_place_id"]),
        Index(value = ["date"]),
        Index(value = ["location_place_id", "date"])  // Composite index
    ]
)
data class PrayerTimesEntity(
    /**
     * Auto-generated primary key. Bu entity'nin kendi unique ID'si.
     *
     * locationPlaceId + date kombinasyonunu primary key yapabilirdik ama
     * Room'un limitation'ları ve code simplicity için auto-generated ID
     * kullanmak daha iyi. Uniqueness'i code level'da kontrol edeceğiz.
     */
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    /**
     * Bu namaz vakitlerinin ait olduğu konumun ID'si.
     * SavedLocation.placeId'ye foreign key.
     *
     * Örnek: 311055 (Ankara, Çankaya)
     *
     * Bu sayede bir konum için tüm vakitleri çekmek çok kolay:
     * SELECT * FROM prayer_times WHERE location_place_id = 311055
     */
    @ColumnInfo(name = "location_place_id")
    val locationPlaceId: Int,

    /**
     * Namaz vakitlerinin tarihi. ISO 8601 formatında: "YYYY-MM-DD"
     * Örnek: "2023-10-29"
     *
     * String kullanmamızın nedenleri:
     * 1. API'den bu formatta geliyor, dönüşüm gereksiz
     * 2. SQLite'ta native Date type yok, text olarak tutuluyor
     * 3. Karşılaştırmalar çalışıyor: "2023-10-29" < "2023-10-30"
     * 4. Kotlin'de LocalDate.parse("2023-10-29") ile kolayca çevrilebilir
     */
    @ColumnInfo(name = "date")
    val date: String,

    /**
     * İmsak vakti. Format: "HH:mm" (24 saat formatı)
     * Örnek: "05:42"
     *
     * İmsak, sahur vaktinin bitişi ve fecir namazının başlangıcıdır.
     * Türkiye'de genellikle güneş doğmadan yaklaşık 1.5 saat öncedir.
     */
    @ColumnInfo(name = "imsak")
    val imsak: String,

    /**
     * Güneş doğuşu vakti. Format: "HH:mm"
     * Örnek: "07:07"
     *
     * Güneş tam doğduğu an. Bu vakitte namaz kılınmaz (mekruh vakit)
     * ama oruç tutan için önemli: güneş doğunca sahur kapanır.
     */
    @ColumnInfo(name = "gunes")
    val gunes: String,

    /**
     * Öğle namazı vakti. Format: "HH:mm"
     * Örnek: "12:37"
     *
     * Güneş tepe noktasından geçtikten sonra başlar.
     * Gölge en kısa olduğu noktada öğle vakti girer.
     */
    @ColumnInfo(name = "ogle")
    val ogle: String,

    /**
     * İkindi namazı vakti. Format: "HH:mm"
     * Örnek: "15:29"
     *
     * Her nesnenin gölgesi kendi boyunun 1 veya 2 misli olduğunda başlar.
     * (Mezheplere göre değişir, API bunu calculation method'a göre hesaplar)
     */
    @ColumnInfo(name = "ikindi")
    val ikindi: String,

    /**
     * Akşam namazı vakti. Format: "HH:mm"
     * Örnek: "17:58"
     *
     * Güneş tamamen battıktan sonra başlar. Bu vakitte iftar yapılır.
     * İftar ezanı = Akşam ezanı
     */
    @ColumnInfo(name = "aksam")
    val aksam: String,

    /**
     * Yatsı namazı vakti. Format: "HH:mm"
     * Örnek: "19:16"
     *
     * Şafak kaybolduğunda, yani tam karanlık olduğunda başlar.
     * Türkiye'de genellikle akşamdan 1-1.5 saat sonradır.
     */
    @ColumnInfo(name = "yatsi")
    val yatsi: String,

    /**
     * Namaz vakitlerinin hangi hesaplama metoduyla bulunduğu.
     *
     * Örnek değerler: "Turkey", "MWL", "ISNA", "Egypt", "Makkah"
     *
     * Her ülke/bölge farklı hesaplama yöntemi kullanır:
     * - Turkey: Diyanet metodunu kullanır
     * - MWL: Muslim World League metodu
     * - ISNA: Islamic Society of North America
     *
     * Bu bilgiyi tutmamızın nedeni: İleride kullanıcı ayarlardan
     * farklı metot seçerse, vakitleri yeniden çekmemiz gerekecek.
     */
    @ColumnInfo(name = "calculation_method")
    val calculationMethod: String = "Turkey",

    /**
     * Timezone offset dakika cinsinden.
     * Örnek: 180 = UTC+3 (Türkiye)
     *
     * Bu değer API isteğinde kullanılan offset. Vakitler bu offset'e
     * göre hesaplanmış. Eğer bu değer değişirse vakitleri yeniden
     * çekmemiz gerekir.
     */
    @ColumnInfo(name = "timezone_offset")
    val timezoneOffset: Int = 180,

    /**
     * Bu kaydın ne zaman çekilip database'e kaydedildiği.
     * Unix timestamp (milliseconds).
     *
     * Kullanım senaryoları:
     * 1. Cache kontrolü: 24 saatten eski kayıtlar stale sayılabilir
     * 2. Debugging: "Bu veri ne zaman çekilmiş?"
     * 3. Offline mode: En son ne zaman internet vardı?
     * 4. Data freshness UI: "Son güncelleme: 2 saat önce"
     */
    @ColumnInfo(name = "fetched_at")
    val fetchedAt: Long = System.currentTimeMillis()
)
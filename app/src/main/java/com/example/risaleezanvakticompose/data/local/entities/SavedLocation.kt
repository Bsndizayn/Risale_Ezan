package com.example.risaleezanvakticompose.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Kullanıcının kaydettiği veya kullandığı konumları temsil eden entity.
 *
 * Bu entity'nin birkaç önemli özelliği var:
 *
 * 1. PRIMARY KEY olarak API'den gelen placeId kullanıyoruz. Böylece aynı yeri
 *    birden fazla kez kaydetmemizi engelliyoruz. Örneğin kullanıcı hem GPS'den
 *    hem de manuel olarak "Ankara, Çankaya" seçerse, ikisi de aynı placeId'ye
 *    sahip olacağından database'de tek kayıt oluşur.
 *
 * 2. isCurrentLocation flag'i GPS'den alınan konumu işaretliyor. Bu sayede
 *    uygulama açıldığında "kullanıcının şu anki konumu hangisi?" sorusuna
 *    hızlıca cevap bulabiliyoruz.
 *
 * 3. isFavorite flag'i kullanıcının favori konumlarını işaretlemek için.
 *    Örneğin kullanıcı İzmir'de yaşıyor ama Ankara'ya seyahat ediyor, her
 *    ikisini de favori olarak işaretleyebilir.
 *
 * 4. lastUpdated timestamp'i bu konumun ne zaman güncellendiğini tutuyor.
 *    Bu önemli çünkü kullanıcı konumlar arasında geçiş yaparken en son
 *    kullanılan konumu gösterebiliyoruz.
 */
@Entity(tableName = "saved_locations")
data class SavedLocation(
    /**
     * API'den gelen unique place ID. Bu bizim primary key'imiz.
     * Örnek: 311055 (Ankara, Çankaya için)
     *
     * AutoGenerate kullanmıyoruz çünkü ID'yi API belirliyor, biz değil.
     * Bu sayede API'den gelen veriyi doğrudan kullanabiliyoruz.
     */
    @PrimaryKey
    @ColumnInfo(name = "place_id")
    val placeId: Int,

    /**
     * Konumun görünen adı. UI'da gösterdiğimiz isim bu.
     * Örnek: "Çankaya", "Keçiören", "Ankara"
     */
    @ColumnInfo(name = "place_name")
    val placeName: String,

    /**
     * Ülke adı. API response'unda "country" olarak geliyor.
     * Örnek: "Türkiye", "Germany"
     *
     * Bu bilgiyi tutmamızın nedeni: Kullanıcı farklı ülkelerdeki yerleri
     * kaydedebilir. Almanya'da yaşayan bir Türk hem Almanya hem Türkiye
     * vakitlerini takip etmek isteyebilir.
     */
    @ColumnInfo(name = "country")
    val country: String,

    /**
     * Bölge/İl adı. API'de "stateName" olarak geliyor.
     * Örnek: "Ankara", "İzmir", "Bayern"
     *
     * Bazı ülkelerde "state", bazılarında "province", bazılarında "region"
     * deniyor ama mantık aynı: ülke altındaki büyük idari birim.
     */
    @ColumnInfo(name = "region")
    val region: String,

    /**
     * ISO 3166-1 alpha-2 ülke kodu. İki harfli standart kod.
     * Örnek: "TR", "DE", "US"
     *
     * Bayrak göstermek veya ülkeye özel işlemler yapmak için kullanılabilir.
     */
    @ColumnInfo(name = "country_code")
    val countryCode: String,

    /**
     * Enlem (Latitude) koordinatı.
     * Örnek: 39.9179 (Ankara, Çankaya)
     *
     * GPS koordinatları namaz vakitlerini hesaplamak için kritik.
     * Vakitler güneşin konumuna göre değişir, bu yüzden tam koordinat gerekir.
     */
    @ColumnInfo(name = "latitude")
    val latitude: Double,

    /**
     * Boylam (Longitude) koordinatı.
     * Örnek: 32.86268 (Ankara, Çankaya)
     */
    @ColumnInfo(name = "longitude")
    val longitude: Double,

    /**
     * Timezone bilgisi. Nullable çünkü API bazen dönmüyor.
     * Örnek: "Europe/Istanbul", "Europe/Berlin"
     *
     * Bu bilgi özellikle yaz/kış saati geçişlerinde önemli. Türkiye'de
     * artık yok ama bazı ülkelerde hala var.
     */
    @ColumnInfo(name = "timezone")
    val timezone: String? = null,

    /**
     * Bu konum GPS'den mi alındı?
     *
     * true: Bu konum kullanıcının cihazının GPS'inden alınmış gerçek konumu.
     * false: Kullanıcı manuel olarak seçmiş.
     *
     * Önemli kural: Aynı anda sadece bir konum isCurrentLocation = true olabilir.
     * Yeni GPS konumu kaydederken önce diğerlerinin flag'ini false yapmalıyız.
     *
     * Bu flag sayesinde uygulama açıldığında "kullanıcı nerede?" sorusuna
     * tek sorgu ile cevap bulabiliyoruz:
     * SELECT * FROM saved_locations WHERE is_current_location = 1 LIMIT 1
     */
    @ColumnInfo(name = "is_current_location")
    val isCurrentLocation: Boolean = false,

    /**
     * Kullanıcı bu konumu favori olarak işaretlemiş mi?
     *
     * Favoriler ekranında sadece isFavorite = true olanları gösterebiliriz.
     * Ayrıca konum seçerken favoriler en üstte listelenebilir.
     */
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,

    /**
     * Bu kaydın en son ne zaman güncellendiği (Unix timestamp - milliseconds).
     *
     * System.currentTimeMillis() kullanarak otomatik set ediyoruz.
     *
     * Kullanım senaryoları:
     * 1. Kullanıcı konumlar arasında geçiş yaptığında, en son kullanılan
     *    konumu "lastUpdated DESC" sorgusu ile bulabiliriz.
     * 2. Eski, kullanılmayan konumları temizlemek için.
     * 3. Veri senkronizasyonu için.
     */
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis()
)
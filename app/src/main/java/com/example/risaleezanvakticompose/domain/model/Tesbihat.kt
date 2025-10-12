package com.example.risaleezanvakticompose.domain.model

/**
 * Tesbihat kategorileri - Her namaz vakti ve özel tesbihatlar
 */
enum class TesbihatCategory(val displayName: String, val description: String) {
    SABAH("Sabah Namazı Tesbihatı", "Sabah namazı sonrası okunan zikirler"),
    OGLE("Öğle Namazı Tesbihatı", "Öğle namazı sonrası okunan zikirler"),
    IKINDI("İkindi Namazı Tesbihatı", "İkindi namazı sonrası okunan zikirler"),
    AKSAM("Akşam Namazı Tesbihatı", "Akşam namazı sonrası okunan zikirler"),
    YATSI("Yatsı Namazı Tesbihatı", "Yatsı namazı sonrası okunan zikirler"),
    GENEL("Genel Tesbihat", "Her namazda ortak okunan zikirler")
}

/**
 * Tesbihat tipi - Zikir mi, dua mı, sure mi?
 */
enum class TesbihatType {
    TESBIH,      // Sübhanallah, Elhamdülillah, Allahu Ekber
    DUA,         // Dualar
    AYET,        // Ayet-el Kürsi gibi ayetler
    SURE,        // İhlas, Felak, Nas
    ZIKIR        // Diğer zikirler
}

/**
 * Tek bir tesbihat öğesi
 */
data class TesbihatItem(
    val id: String,
    val title: String,                    // "Sübhanallah" veya "Ayet-el Kürsi"
    val arabicText: String,               // Arapça metin
    val transcription: String? = null,    // Okunuşu (opsiyonel)
    val translation: String? = null,      // Türkçe anlamı (opsiyonel)
    val count: Int = 1,                   // Kaç kere okunacak (33, 34, 1, 3, vs.)
    val type: TesbihatType,
    val order: Int                        // Sıralama için
)

/**
 * Bir kategorinin tüm tesbihat listesi
 */
data class TesbihatContent(
    val category: TesbihatCategory,
    val items: List<TesbihatItem>
)

/**
 * Kullanıcının tesbihat ilerleme durumu
 */
data class TesbihatProgress(
    val categoryId: String,               // Hangi kategori
    val itemId: String,                   // Hangi tesbihat öğesi
    val currentCount: Int,                // Şu an kaç tane okundu
    val totalCount: Int,                  // Toplam kaç tane okunacak
    val isCompleted: Boolean,             // Tamamlandı mı
    val lastUpdated: Long = System.currentTimeMillis()
)
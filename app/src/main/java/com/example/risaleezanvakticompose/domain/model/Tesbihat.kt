package com.example.risaleezanvakticompose.domain.model

enum class TesbihatCategory(val displayName: String, val description: String) {
    SABAH("Sabah Namazı Tesbihatı", "Sabah namazı sonrası okunan zikirler"),
    OGLE("Öğle Namazı Tesbihatı", "Öğle namazı sonrası okunan zikirler"),
    IKINDI("İkindi Namazı Tesbihatı", "İkindi namazı sonrası okunan zikirler"),
    AKSAM("Akşam Namazı Tesbihatı", "Akşam namazı sonrası okunan zikirler"),
    YATSI("Yatsı Namazı Tesbihatı", "Yatsı namazı sonrası okunan zikirler"),
    GENEL("Genel Tesbihat", "Her namazda ortak okunan zikirler")
}


enum class TesbihatType {
    TESBIH,
    DUA,
    AYET,
    SURE,
    ZIKIR
}


data class TesbihatItem(
    val id: String,
    val title: String,
    val arabicText: String,
    val transcription: String? = null,
    val translation: String? = null,
    val count: Int = 1,
    val type: TesbihatType,
    val order: Int
)


data class TesbihatContent(
    val category: TesbihatCategory,
    val items: List<TesbihatItem>
)

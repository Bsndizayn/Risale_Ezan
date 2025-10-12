package com.example.risaleezanvakticompose.util

object CountryTranslator {

    private val translations = mapOf(
        "Turkey" to "Türkiye",
        // Diğer ülkeler için de eklemeler yapılabilir
    )

    fun translate(englishName: String): String {
        return translations[englishName] ?: englishName
    }

}
package com.example.risaleezanvakticompose.data.model

import com.example.risaleezanvakticompose.util.CountryTranslator

data class CountriesItem(
    val code: String,
    val name: String
) {
    /**
     * UI'da gösterilecek adı döndürür (Türkçe çevirili)
     */
    val displayName: String
        get() = CountryTranslator.translate(name)

    /**
     * API'ye gönderilecek adı döndürür (İngilizce)
     */
    val apiName: String
        get() = name
}
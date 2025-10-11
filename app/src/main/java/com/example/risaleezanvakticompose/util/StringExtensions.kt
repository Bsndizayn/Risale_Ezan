package com.example.risaleezanvakticompose.util

import java.util.Locale

fun String.normalizeForSearch(): String {
    return this
        .replace('İ', 'i')
        .replace('I', 'i')
        .replace('Ş', 's')
        .replace('Ğ', 'g')
        .replace('Ç', 'c')
        .replace('Ö', 'o')
        .replace('Ü', 'u')
        .lowercase(Locale.ENGLISH)
        .replace('ı', 'i')
        .replace('ş', 's')
        .replace('ğ', 'g')
        .replace('ç', 'c')
        .replace('ö', 'o')
        .replace('ü', 'u')
        .trim()
}
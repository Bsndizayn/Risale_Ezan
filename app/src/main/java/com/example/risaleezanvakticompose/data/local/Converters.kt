package com.example.risaleezanvakticompose.data.local

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class Converters {


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

    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.format(DateTimeFormatter.ISO_LOCAL_TIME)
        // Örnek: LocalTime.of(12, 37) → "12:37:00"
    }

    @TypeConverter
    fun toLocalTime(time: String?): LocalTime? {
        return time?.let { LocalTime.parse(it) }
        // Örnek: "12:37" → LocalTime(12, 37)
    }

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        // Örnek: LocalDateTime.of(2023, 10, 29, 14, 30) → "2023-10-29T14:30:00"
    }

    @TypeConverter
    fun toLocalDateTime(dateTime: String?): LocalDateTime? {
        return dateTime?.let { LocalDateTime.parse(it) }
        // Örnek: "2023-10-29T14:30:00" → LocalDateTime(2023, 10, 29, 14, 30, 0)
    }

    @TypeConverter
    fun fromInstant(instant: Instant?): Long? {
        return instant?.toEpochMilli()
        // Örnek: Instant.now() → 1698588600000
    }

    @TypeConverter
    fun toInstant(timestamp: Long?): Instant? {
        return timestamp?.let { Instant.ofEpochMilli(it) }
        // Örnek: 1698588600000 → Instant(2023-10-29T14:30:00Z)
    }

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

    @TypeConverter
    fun toStringList(string: String?): List<String>? {
        return string
            ?.removeSurrounding("[", "]")  // Bracket'leri kaldır
            ?.split(",")                    // Virgüllerle böl
            ?.map { it.trim().removeSurrounding("\"") }  // Her elemanı temizle
            ?.filter { it.isNotEmpty() }   // Boş elemanları filtrele
        // Örnek: "[\"Ankara\",\"Angora\"]" → ["Ankara", "Angora"]
    }

}
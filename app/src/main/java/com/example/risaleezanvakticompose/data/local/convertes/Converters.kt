package com.example.risaleezanvakticompose.data.local.convertes

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class Converters {


    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(DateTimeFormatter.ISO_LOCAL_DATE)
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
    }

    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.format(DateTimeFormatter.ISO_LOCAL_TIME)
    }

    @TypeConverter
    fun toLocalTime(time: String?): LocalTime? {
        return time?.let { LocalTime.parse(it) }
    }

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    @TypeConverter
    fun toLocalDateTime(dateTime: String?): LocalDateTime? {
        return dateTime?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun fromInstant(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }

    @TypeConverter
    fun toInstant(timestamp: Long?): Instant? {
        return timestamp?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return list?.joinToString(
            separator = ",",
            prefix = "[",
            postfix = "]",
            transform = { "\"$it\"" }
        )
    }

    @TypeConverter
    fun toStringList(string: String?): List<String>? {
        return string
            ?.removeSurrounding("[", "]")
            ?.split(",")
            ?.map { it.trim().removeSurrounding("\"") }
            ?.filter { it.isNotEmpty() }
    }

}
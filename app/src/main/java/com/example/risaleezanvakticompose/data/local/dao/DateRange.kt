package com.example.risaleezanvakticompose.data.local.dao


/**
 * MIN ve MAX date query'si için data class.
 *
 * Room bu struct'ı otomatik map ediyor. SQL'den gelen iki kolon
 * (minDate, maxDate) bu data class'ın property'lerine eşleşiyor.
 *
 * Nullable çünkü tablo boşsa MIN ve MAX null döner.
 */
data class DateRange(
    val minDate: String?,
    val maxDate: String?
)
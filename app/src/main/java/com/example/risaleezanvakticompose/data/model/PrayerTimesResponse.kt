package com.example.risaleezanvakticompose.data.model

data class PrayerTimesResponse(
    val place: Place,
    val times: Map<String, List<String>>
)

/**
 * Bir günün namaz vakitlerini tutan yardımcı data class
 * API'den gelen ham listeyi daha anlamlı bir yapıya dönüştürmek için kullanılıyor
 */
data class DailyPrayerTimes(
    val date: String,           // Tarih: "2025-10-05"
    val imsak: String,          // İmsak vakti: "05:42"
    val gunes: String,          // Güneş vakti: "07:07"
    val ogle: String,           // Öğle vakti: "12:37"
    val ikindi: String,         // İkindi vakti: "15:29"
    val aksam: String,          // Akşam vakti: "17:58"
    val yatsi: String           // Yatsı vakti: "19:16"
) {
    companion object {
        /**
         * API'den gelen List<String> formatındaki veriyi DailyPrayerTimes objesine dönüştürür
         *
         * @param date Tarih bilgisi
         * @param timesList 6 elemanlı liste: [imsak, gunes, ogle, ikindi, aksam, yatsi]
         * @return DailyPrayerTimes objesi veya liste geçersizse null
         */
        fun fromList(date: String, timesList: List<String>): DailyPrayerTimes? {
            // Liste tam 6 eleman içermiyorsa null döndür
            if (timesList.size != 6) return null

            return DailyPrayerTimes(
                date = date,
                imsak = timesList[0],
                gunes = timesList[1],
                ogle = timesList[2],
                ikindi = timesList[3],
                aksam = timesList[4],
                yatsi = timesList[5]
            )
        }
    }
}
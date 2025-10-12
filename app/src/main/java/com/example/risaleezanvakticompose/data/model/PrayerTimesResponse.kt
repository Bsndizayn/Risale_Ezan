package com.example.risaleezanvakticompose.data.model

data class PrayerTimesResponse(
    val place: Place,
    val times: Map<String, List<String>>
)

data class DailyPrayerTimes(
    val date: String,
    val imsak: String,
    val gunes: String,
    val ogle: String,
    val ikindi: String,
    val aksam: String,
    val yatsi: String
) {
    companion object {

        fun fromList(date: String, timesList: List<String>): DailyPrayerTimes? {
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
package com.example.risaleezan // Paket adını kontrol et

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class PrayerTime(val name: String, val time: String)

// "val" yerine "var" kullandık
class PrayerTimesAdapter(private var prayerTimes: List<PrayerTime>) :
    RecyclerView.Adapter<PrayerTimesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val prayerName: TextView = view.findViewById(R.id.textViewPrayerNameItem)
        val prayerTime: TextView = view.findViewById(R.id.textViewPrayerTimeItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.prayer_time_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val prayer = prayerTimes[position]
        holder.prayerName.text = prayer.name
        holder.prayerTime.text = prayer.time
    }

    override fun getItemCount() = prayerTimes.size

    // YENİ EKLENEN FONKSİYON
    fun updateData(newPrayerTimes: List<PrayerTime>) {
        (this.prayerTimes as MutableList<PrayerTime>).clear()
        (this.prayerTimes as MutableList<PrayerTime>).addAll(newPrayerTimes)
        notifyDataSetChanged()
    }
}
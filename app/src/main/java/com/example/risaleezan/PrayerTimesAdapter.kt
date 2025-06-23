package com.example.risaleezan // Paket adını kontrol et

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

data class PrayerTime(val name: String, val time: String)

class PrayerTimesAdapter(private var prayerTimes: List<PrayerTime>) :
    RecyclerView.Adapter<PrayerTimesAdapter.ViewHolder>() {

    private lateinit var prefs: SharedPreferences
    private val PREFS_NAME = "PrayerTimeSettings"

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val prayerName: TextView = view.findViewById(R.id.textViewPrayerNameItem)
        val prayerTime: TextView = view.findViewById(R.id.textViewPrayerTimeItem)
        val notificationIcon: ImageView = view.findViewById(R.id.imageViewNotificationToggle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        prefs = parent.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.prayer_time_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val prayer = prayerTimes[position]
        holder.prayerName.text = prayer.name
        holder.prayerTime.text = prayer.time

        val preferenceKey = "alarm_${prayer.name.capitalizeAsCustom()}_enabled"

        val isEnabled = prefs.getBoolean(preferenceKey, true)
        updateIcon(holder.notificationIcon, isEnabled)

        holder.notificationIcon.setOnClickListener {
            val newState = !prefs.getBoolean(preferenceKey, true)
            prefs.edit().putBoolean(preferenceKey, newState).apply()
            updateIcon(holder.notificationIcon, newState)
        }
    }

    private fun updateIcon(imageView: ImageView, isEnabled: Boolean) {
        if (isEnabled) {
            imageView.setImageResource(android.R.drawable.ic_lock_idle_alarm)
            imageView.setColorFilter(ContextCompat.getColor(imageView.context, R.color.light_gold))
        } else {
            imageView.setImageResource(android.R.drawable.ic_notification_clear_all)
            imageView.setColorFilter(ContextCompat.getColor(imageView.context, R.color.off_white))
        }
    }

    override fun getItemCount() = prayerTimes.size

    fun updateData(newPrayerTimes: List<PrayerTime>) {
        (this.prayerTimes as MutableList<PrayerTime>).clear()
        (this.prayerTimes as MutableList<PrayerTime>).addAll(newPrayerTimes)
        notifyDataSetChanged()
    }

    // GÜNCELLENMİŞ FONKSİYON
    private fun String.capitalizeAsCustom(): String {
        return this.replace("İ", "I")
            .replace("Ğ", "G")
            .replace("Ş", "S")
            .lowercase(Locale.getDefault())
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}
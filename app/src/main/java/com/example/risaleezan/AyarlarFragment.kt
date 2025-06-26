package com.example.risaleezan

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView

class AyarlarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ayarlar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // "Şehir Değiştir" kartına tıklandığında
        val cardCitySettings = view.findViewById<MaterialCardView>(R.id.cardCitySettings)
        cardCitySettings.setOnClickListener {
            findNavController().navigate(R.id.action_ayarlarFragment_to_citiesFragment)
        }

        // "Bildirim Ayarları" kartına tıklandığında
        val cardNotificationSettings = view.findViewById<MaterialCardView>(R.id.cardNotificationSettings)
        cardNotificationSettings.setOnClickListener {
            // Önce izinlerin tam olup olmadığını kontrol et
            if (areAllPermissionsGranted(requireContext())) {
                // Eğer tüm izinler tamamsa, doğrudan ses ayarlarına git
                findNavController().navigate(R.id.action_ayarlarFragment_to_bildirimAyarlariFragment)
            } else {
                // Eğer eksik izin varsa, önce izin kurulum ekranına git
                findNavController().navigate(R.id.action_ayarlarFragment_to_permissionSetupFragment)
            }
        }
    }

    // İzinleri kontrol eden yardımcı fonksiyon
    private fun areAllPermissionsGranted(context: Context): Boolean {
        // 1. Bildirim İzni (Android 13+)
        val notificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Eski sürümler için bu izin yok, tam kabul et
        }

        // 2. Pil Optimizasyonu İzni (Android 6+)
        val batteryOptimizationIgnored = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
        } else {
            true
        }

        // 3. Tam Zamanlı Alarm İzni (Android 12+)
        val alarmGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }

        return notificationGranted && batteryOptimizationIgnored && alarmGranted
    }
}
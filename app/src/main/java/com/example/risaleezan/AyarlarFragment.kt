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
import android.util.Log // Log için eklendi

class AyarlarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("AyarlarFragment", "onCreateView çağrıldı.") // EKLE
        return inflater.inflate(R.layout.fragment_ayarlar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("AyarlarFragment", "onViewCreated çağrıldı.") // EKLE

        // "Şehir Değiştir" kartına tıklandığında
        val cardCitySettings = view.findViewById<MaterialCardView>(R.id.cardCitySettings)
        cardCitySettings.setOnClickListener {
            Log.d("AyarlarFragment", "Şehir Değiştir kartına tıklandı.")
            findNavController().navigate(R.id.action_ayarlarFragment_to_citiesFragment)
        }

        // "Bildirim Ayarları" kartına tıklandığında
        val cardNotificationSettings = view.findViewById<MaterialCardView>(R.id.cardNotificationSettings)
        cardNotificationSettings.setOnClickListener {
            Log.d("AyarlarFragment", "Bildirim Ayarları kartına tıklandı.")
            // Önce izinlerin tam olup olmadığını kontrol et
            if (areAllPermissionsGranted(requireContext())) {
                Log.d("AyarlarFragment", "Tüm izinler verildi, Bildirim Ayarları'na gidiliyor.")
                // Eğer tüm izinler tamamsa, doğrudan ses ayarlarına git
                findNavController().navigate(R.id.action_ayarlarFragment_to_bildirimAyarlariFragment)
            } else {
                Log.d("AyarlarFragment", "Eksik izinler var, İzin Kurulumu ekranına gidiliyor.")
                // Eğer eksik izin varsa, önce izin kurulum ekranına git
                findNavController().navigate(R.id.action_ayarlarFragment_to_permissionSetupFragment)
            }
        }
    }

    // ... (areAllPermissionsGranted fonksiyonunuzun geri kalanı)

    private fun areAllPermissionsGranted(context: Context): Boolean {
        // ... (Bu fonksiyonunuzun içeriği değişmedi)
        val notificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            Log.d("AyarlarFragment", "Bildirim İzni: $granted")
            granted
        } else {
            true
        }

        val batteryOptimizationIgnored = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val ignored = powerManager.isIgnoringBatteryOptimizations(context.packageName)
            Log.d("AyarlarFragment", "Pil Optimizasyonu İzni: $ignored")
            ignored
        } else {
            true
        }

        val alarmGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val granted = alarmManager.canScheduleExactAlarms()
            Log.d("AyarlarFragment", "Alarm İzni: $granted")
            granted
        } else {
            true
        }

        val allGranted = notificationGranted && batteryOptimizationIgnored && alarmGranted
        Log.d("AyarlarFragment", "Tüm İzinler Verildi mi?: $allGranted")
        return allGranted
    }
}
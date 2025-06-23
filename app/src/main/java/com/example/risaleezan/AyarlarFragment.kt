package com.example.risaleezan

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
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController

class AyarlarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ayarlar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonSelectCity = view.findViewById<Button>(R.id.buttonSelectCity)
        buttonSelectCity.setOnClickListener {
            findNavController().navigate(R.id.action_ayarlarFragment_to_citiesFragment)
        }

        val buttonNotificationSettings = view.findViewById<Button>(R.id.buttonNotificationSettings)
        buttonNotificationSettings.setOnClickListener {
            if (areAllPermissionsGranted(requireContext())) {
                findNavController().navigate(R.id.action_ayarlarFragment_to_bildirimAyarlariFragment)
            } else {
                findNavController().navigate(R.id.action_ayarlarFragment_to_permissionSetupFragment)
            }
        }
    }

    private fun areAllPermissionsGranted(context: Context): Boolean {
        val notificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else { true }

        val batteryOptimizationIgnored = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
        } else { true }

        val alarmGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else { true }

        return notificationGranted && batteryOptimizationIgnored && alarmGranted
    }
}
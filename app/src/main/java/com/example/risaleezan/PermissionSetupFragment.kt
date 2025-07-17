package com.example.risaleezan

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView

class PermissionSetupFragment : Fragment() {

    private lateinit var tvNotificationStatus: TextView
    private lateinit var btnRequestNotification: Button
    private lateinit var tvBatteryStatus: TextView
    private lateinit var btnRequestBattery: Button
    private lateinit var tvAlarmStatus: TextView
    private lateinit var btnRequestAlarm: Button
    private lateinit var tvAllPermissionsStatus: TextView
    private lateinit var btnComplete: Button

    private lateinit var cardAutostart: MaterialCardView
    private lateinit var btnRequestAutostart: Button

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            updatePermissionStatus()
            if (!isGranted) {
                Toast.makeText(
                    requireContext(),
                    "Bildirim izni reddedildi. Ezan bildirimleri çalışmayabilir.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_permission_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvNotificationStatus = view.findViewById(R.id.tvNotificationStatus)
        btnRequestNotification = view.findViewById(R.id.btnRequestNotification)
        tvBatteryStatus = view.findViewById(R.id.tvBatteryStatus)
        btnRequestBattery = view.findViewById(R.id.btnRequestBattery)
        tvAlarmStatus = view.findViewById(R.id.tvAlarmStatus)
        btnRequestAlarm = view.findViewById(R.id.btnRequestAlarm)
        tvAllPermissionsStatus = view.findViewById(R.id.tvAllPermissionsStatus)
        btnComplete = view.findViewById(R.id.buttonComplete)

        cardAutostart = view.findViewById(R.id.cardAutostart)
        btnRequestAutostart = view.findViewById(R.id.btnRequestAutostart)

        setupClickListeners()
        updatePermissionStatus()
    }

    private fun setupClickListeners() {
        btnRequestNotification.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                openAppSettings()
            }
        }

        btnRequestBattery.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:${requireContext().packageName}")
                }
                startActivity(intent)
            }
        }

        btnRequestAlarm.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            } else {
                openAppSettings()
            }
        }

        btnRequestAutostart.setOnClickListener {
            openAppSettings()
            Toast.makeText(
                requireContext(),
                "Otomatik başlatma ayarını manuel açmalısınız.",
                Toast.LENGTH_LONG
            ).show()
        }

        btnComplete.setOnClickListener {
            if (areAllPermissionsGranted()) {
                findNavController().navigate(R.id.action_permissionSetupFragment_to_bildirimAyarlariFragment)
            } else {
                Toast.makeText(requireContext(), "Lütfen eksik izinleri verin.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun areAllPermissionsGranted(): Boolean {
        val notificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true

        val batteryOptimizationIgnored = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager =
                requireContext().getSystemService(Context.POWER_SERVICE) as PowerManager
            powerManager.isIgnoringBatteryOptimizations(requireContext().packageName)
        } else true

        val alarmGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager =
                requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else true

        return notificationGranted && batteryOptimizationIgnored && alarmGranted
    }

    override fun onResume() {
        super.onResume()
        updatePermissionStatus()
    }

    private fun updatePermissionStatus() {
        // Bildirim
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted =
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            tvNotificationStatus.text = if (granted) "Verildi" else "Gerekiyor"
            tvNotificationStatus.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (granted) android.R.color.holo_green_dark else android.R.color.holo_red_dark
                )
            )
            btnRequestNotification.isEnabled = !granted
        } else {
            tvNotificationStatus.text = "Gerekli Değil"
            btnRequestNotification.isEnabled = false
        }

        // Pil
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager =
                requireContext().getSystemService(Context.POWER_SERVICE) as PowerManager
            val ignored = powerManager.isIgnoringBatteryOptimizations(requireContext().packageName)
            tvBatteryStatus.text = if (ignored) "Verildi" else "Gerekiyor"
            tvBatteryStatus.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (ignored) android.R.color.holo_green_dark else android.R.color.holo_red_dark
                )
            )
            btnRequestBattery.isEnabled = !ignored
        } else {
            tvBatteryStatus.text = "Gerekli Değil"
            btnRequestBattery.isEnabled = false
        }

        // Alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager =
                requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val granted = alarmManager.canScheduleExactAlarms()
            tvAlarmStatus.text = if (granted) "Verildi" else "Gerekiyor"
            tvAlarmStatus.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (granted) android.R.color.holo_green_dark else android.R.color.holo_red_dark
                )
            )
            btnRequestAlarm.isEnabled = !granted
        } else {
            tvAlarmStatus.text = "Gerekli Değil"
            btnRequestAlarm.isEnabled = false
        }

        // ✅ GENEL DURUM
        val allGranted = areAllPermissionsGranted()
        tvAllPermissionsStatus.text = if (allGranted) "Tüm İzinler Verildi!" else "Eksik İzinler Var"
        tvAllPermissionsStatus.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (allGranted) android.R.color.holo_green_dark else android.R.color.holo_red_dark
            )
        )
        btnComplete.isEnabled = allGranted
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireContext().packageName, null)
        }
        startActivity(intent)
    }
}

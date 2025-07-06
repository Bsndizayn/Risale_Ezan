package com.example.risaleezan

import android.app.AlarmManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController

class PermissionSetupFragment : Fragment() {

    private lateinit var switchNotification: SwitchCompat
    private lateinit var switchAlarms: SwitchCompat
    private lateinit var switchBattery: SwitchCompat
    private lateinit var switchAutostart: SwitchCompat
    private lateinit var buttonComplete: Button

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            Log.d("PermissionSetup", "Bildirim izni sonucu: $granted")
            updateUi()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_permission_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        switchNotification = view.findViewById(R.id.switchNotification)
        switchAlarms = view.findViewById(R.id.switchAlarms)
        switchBattery = view.findViewById(R.id.switchBattery)
        switchAutostart = view.findViewById(R.id.switchAutostart)
        buttonComplete = view.findViewById(R.id.buttonComplete)

        setupListeners()
        updateUi()
    }

    override fun onResume() {
        super.onResume()
        updateUi()
    }

    private fun setupListeners() {
        // Notification permission switch
        switchNotification.setOnCheckedChangeListener { _, isChecked ->
            Log.d("PermissionSetup", "Bildirim switch tıklandı: $isChecked")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (isChecked) {
                    val currentPermission = ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

                    if (!currentPermission) {
                        Log.d("PermissionSetup", "İzin isteniyor...")
                        notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
            updateButtonState() // Her switch değişikliğinde buton durumunu güncelle
        }

        // Alarm permission switch
        switchAlarms.setOnCheckedChangeListener { _, isChecked ->
            Log.d("PermissionSetup", "Alarm switch tıklandı: $isChecked")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (isChecked) {
                    val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    if (!alarmManager.canScheduleExactAlarms()) {
                        Log.d("PermissionSetup", "Alarm ayarları açılıyor...")
                        try {
                            startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                        } catch (e: Exception) {
                            Log.e("PermissionSetup", "Alarm ayarları açılamadı", e)
                            Toast.makeText(requireContext(), "Ayarlar açılamadı", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            updateButtonState()
        }

        // Battery optimization switch
        switchBattery.setOnCheckedChangeListener { _, isChecked ->
            Log.d("PermissionSetup", "Batarya switch tıklandı: $isChecked")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (isChecked) {
                    val powerManager = requireContext().getSystemService(Context.POWER_SERVICE) as PowerManager
                    if (!powerManager.isIgnoringBatteryOptimizations(requireContext().packageName)) {
                        Log.d("PermissionSetup", "Batarya ayarları açılıyor...")
                        try {
                            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                            intent.data = Uri.parse("package:${requireContext().packageName}")
                            startActivity(intent)
                        } catch (e: Exception) {
                            Log.e("PermissionSetup", "Batarya ayarları açılamadı", e)
                            Toast.makeText(requireContext(), "Ayarlar açılamadı", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            updateButtonState()
        }

        // Autostart switch
        switchAutostart.setOnCheckedChangeListener { _, isChecked ->
            Log.d("PermissionSetup", "Autostart switch tıklandı: $isChecked")
            if (isChecked) {
                openAutostartSettings()
                Toast.makeText(requireContext(), "Otomatik başlatmayı açtıysanız kutucuğu işaretli bırakın.", Toast.LENGTH_LONG).show()
            }
            updateButtonState() // Autostart değişikliğinde de güncelle
        }

        buttonComplete.setOnClickListener {
            if (checkAllPermissionsGranted()) {
                findNavController().navigate(R.id.action_permissionSetupFragment_to_bildirimAyarlariFragment)
            } else {
                val missingPermissions = getMissingPermissions()
                Toast.makeText(requireContext(), "Eksik izinler: $missingPermissions", Toast.LENGTH_LONG).show()
                updateUi()
            }
        }
    }

    private fun updateUi() {
        val context = requireContext()
        Log.d("PermissionSetup", "UI güncelleniyor...")

        // Bildirim izni
        val isNotificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Eski sürümler için otomatik true
        }

        Log.d("PermissionSetup", "Bildirim izni durumu: $isNotificationGranted")

        // Listener'ı geçici olarak kaldır, switch'i güncelle, sonra geri ekle
        switchNotification.setOnCheckedChangeListener(null)
        switchNotification.isChecked = isNotificationGranted
        switchNotification.isEnabled = true
        setupNotificationSwitchListener()

        // Alarm izni
        val isAlarmGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true // Eski sürümler için otomatik true
        }

        Log.d("PermissionSetup", "Alarm izni durumu: $isAlarmGranted")

        switchAlarms.setOnCheckedChangeListener(null)
        switchAlarms.isChecked = isAlarmGranted
        switchAlarms.isEnabled = true
        setupAlarmSwitchListener()

        // Batarya optimizasyonu
        val isBatteryOk = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
        } else {
            true // Eski sürümler için otomatik true
        }

        Log.d("PermissionSetup", "Batarya izni durumu: $isBatteryOk")

        switchBattery.setOnCheckedChangeListener(null)
        switchBattery.isChecked = isBatteryOk
        switchBattery.isEnabled = true
        setupBatterySwitchListener()

        // Autostart - değişiklik yok, kullanıcı manuel işaretleyecek
        switchAutostart.isEnabled = true
        setupAutostartSwitchListener()

        // Buton durumu
        updateButtonState()
    }

    private fun updateButtonState() {
        val allGranted = checkAllPermissionsGranted()
        buttonComplete.isEnabled = allGranted

        Log.d("PermissionSetup", "Buton durumu güncelleniyor: $allGranted")
        Log.d("PermissionSetup", "Bildirim: ${switchNotification.isChecked}")
        Log.d("PermissionSetup", "Alarm: ${switchAlarms.isChecked}")
        Log.d("PermissionSetup", "Batarya: ${switchBattery.isChecked}")
        Log.d("PermissionSetup", "Autostart: ${switchAutostart.isChecked}")
    }

    private fun getMissingPermissions(): String {
        val missing = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                missing.add("Bildirim")
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                missing.add("Alarm")
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = requireContext().getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!powerManager.isIgnoringBatteryOptimizations(requireContext().packageName)) {
                missing.add("Batarya")
            }
        }

        if (!switchAutostart.isChecked) {
            missing.add("Otomatik Başlatma")
        }

        return missing.joinToString(", ")
    }

    private fun setupNotificationSwitchListener() {
        switchNotification.setOnCheckedChangeListener { _, isChecked ->
            Log.d("PermissionSetup", "Bildirim switch listener tetiklendi: $isChecked")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (isChecked) {
                    val currentPermission = ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

                    if (!currentPermission) {
                        Log.d("PermissionSetup", "İzin isteniyor...")
                        notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
            updateButtonState()
        }
    }

    private fun setupAlarmSwitchListener() {
        switchAlarms.setOnCheckedChangeListener { _, isChecked ->
            Log.d("PermissionSetup", "Alarm switch listener tetiklendi: $isChecked")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (isChecked) {
                    val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    if (!alarmManager.canScheduleExactAlarms()) {
                        Log.d("PermissionSetup", "Alarm ayarları açılıyor...")
                        try {
                            startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                        } catch (e: Exception) {
                            Log.e("PermissionSetup", "Alarm ayarları açılamadı", e)
                            Toast.makeText(requireContext(), "Ayarlar açılamadı", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            updateButtonState()
        }
    }

    private fun setupBatterySwitchListener() {
        switchBattery.setOnCheckedChangeListener { _, isChecked ->
            Log.d("PermissionSetup", "Batarya switch listener tetiklendi: $isChecked")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (isChecked) {
                    val powerManager = requireContext().getSystemService(Context.POWER_SERVICE) as PowerManager
                    if (!powerManager.isIgnoringBatteryOptimizations(requireContext().packageName)) {
                        Log.d("PermissionSetup", "Batarya ayarları açılıyor...")
                        try {
                            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                            intent.data = Uri.parse("package:${requireContext().packageName}")
                            startActivity(intent)
                        } catch (e: Exception) {
                            Log.e("PermissionSetup", "Batarya ayarları açılamadı", e)
                            Toast.makeText(requireContext(), "Ayarlar açılamadı", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            updateButtonState()
        }
    }

    private fun setupAutostartSwitchListener() {
        switchAutostart.setOnCheckedChangeListener { _, isChecked ->
            Log.d("PermissionSetup", "Autostart switch listener tetiklendi: $isChecked")
            if (isChecked) {
                openAutostartSettings()
                Toast.makeText(requireContext(), "Otomatik başlatmayı açtıysanız kutucuğu işaretli bırakın.", Toast.LENGTH_LONG).show()
            }
            updateButtonState()
        }
    }

    private fun checkAllPermissionsGranted(): Boolean {
        val context = requireContext()

        val isNotificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else true

        val isAlarmGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else true

        val isBatteryOptimizationIgnored = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
        } else true

        val isAutostartAccepted = switchAutostart.isChecked

        val result = isNotificationGranted && isAlarmGranted && isBatteryOptimizationIgnored && isAutostartAccepted

        Log.d("PermissionSetup", "İzin kontrolü sonucu: $result")
        Log.d("PermissionSetup", "- Bildirim: $isNotificationGranted")
        Log.d("PermissionSetup", "- Alarm: $isAlarmGranted")
        Log.d("PermissionSetup", "- Batarya: $isBatteryOptimizationIgnored")
        Log.d("PermissionSetup", "- Autostart: $isAutostartAccepted")

        return result
    }

    private fun openAutostartSettings() {
        val intents = listOf(
            Intent().setComponent(ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            Intent().setComponent(ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            Intent().setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            Intent().setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            Intent().setComponent(ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            Intent().setComponent(ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            Intent().setComponent(ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            Intent().setComponent(ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            Intent().setComponent(ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")).setData(Uri.parse("mobilemanager://function/entry/AutoStart"))
        )
        for (intent in intents) {
            if (requireActivity().packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                startActivity(intent)
                return
            }
        }
        Toast.makeText(requireContext(), "Otomatik başlatma ayarları bulunamadı", Toast.LENGTH_SHORT).show()
    }
}
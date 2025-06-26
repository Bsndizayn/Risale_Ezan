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
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
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
        switchNotification.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        switchAlarms.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            }
        }

        switchBattery.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                intent.data = Uri.parse("package:${requireContext().packageName}")
                startActivity(intent)
            }
        }

        switchAutostart.setOnClickListener {
            openAutostartSettings()
            Toast.makeText(requireContext(), "Otomatik başlatmayı açtıysanız kutucuğu işaretleyin.", Toast.LENGTH_LONG).show()
        }

        buttonComplete.setOnClickListener {
            if (checkAllPermissionsGranted()) {
                findNavController().navigate(R.id.action_permissionSetupFragment_to_bildirimAyarlariFragment)
            } else {
                Toast.makeText(requireContext(), "Lütfen tüm izinleri verdikten sonra tekrar deneyin.", Toast.LENGTH_SHORT).show()
                updateUi()
            }
        }
    }

    private fun updateUi() {
        val context = requireContext()

        // Bildirim izni
        val isNotificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else true
        switchNotification.isChecked = isNotificationGranted
        switchNotification.isEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

        // Alarm izni
        val isAlarmGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else true
        switchAlarms.isChecked = isAlarmGranted
        switchAlarms.isEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

        // Batarya optimizasyonu
        val isBatteryOk = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
        } else true
        switchBattery.isChecked = isBatteryOk
        switchBattery.isEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

        // Autostart: elle kontrol
        // switchAutostart elle işaretlenecek

        // Tüm izinler verilmişse butonu aktif et
        buttonComplete.isEnabled = checkAllPermissionsGranted()
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

        val isAutostartAccepted = switchAutostart.isChecked // elle işaretlenir

        return isNotificationGranted && isAlarmGranted && isBatteryOptimizationIgnored && isAutostartAccepted
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
    }
}

package com.example.risaleezanvakticompose.util

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

sealed class PermissionState {
    object GRANTED : PermissionState()
    object DENIED : PermissionState()
    object PERMANENTLY_DENIED : PermissionState()
}

@Singleton
class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // SharedPreferences için sabitler
    private companion object {
        const val PREFS_NAME = "permission_prefs"
        const val KEY_LOCATION_PERMISSION_REQUESTED = "location_permission_requested"
        const val KEY_NOTIFICATION_PERMISSION_REQUESTED = "notification_permission_requested"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getLocationPermissionState(activity: Activity): PermissionState {
        return when {
            // İzin verilmişse
            isLocationPermissionGranted() -> PermissionState.GRANTED

            // İzin daha önce hiç istenmemişse
            !hasLocationPermissionBeenRequested() -> PermissionState.DENIED

            // shouldShowRequestPermissionRationale true dönüyorsa -> kullanıcı reddetti ama kalıcı değil
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> PermissionState.DENIED

            // shouldShowRequestPermissionRationale false ama izin daha önce istenmişse -> kalıcı reddedilmiş
            else -> PermissionState.PERMANENTLY_DENIED
        }
    }

    fun markLocationPermissionRequested() {
        prefs.edit().putBoolean(KEY_LOCATION_PERMISSION_REQUESTED, true).apply()
    }

    private fun hasLocationPermissionBeenRequested(): Boolean {
        return prefs.getBoolean(KEY_LOCATION_PERMISSION_REQUESTED, false)
    }

    fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun getNotificationPermissionState(activity: Activity): PermissionState {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return PermissionState.GRANTED
        }

        return when {
            // İzin verilmişse
            isNotificationPermissionGranted() -> PermissionState.GRANTED

            // İzin daha önce hiç istenmemişse
            !hasNotificationPermissionBeenRequested() -> PermissionState.DENIED

            // shouldShowRequestPermissionRationale true dönüyorsa -> kullanıcı reddetti ama kalıcı değil
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            ) -> PermissionState.DENIED

            // shouldShowRequestPermissionRationale false ama izin daha önce istenmişse -> kalıcı reddedilmiş
            else -> PermissionState.PERMANENTLY_DENIED
        }
    }

    fun markNotificationPermissionRequested() {
        prefs.edit().putBoolean(KEY_NOTIFICATION_PERMISSION_REQUESTED, true).apply()
    }

    private fun hasNotificationPermissionBeenRequested(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATION_PERMISSION_REQUESTED, false)
    }

    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    fun isBatteryOptimizationDisabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
        } else {
            true
        }
    }

    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun openBatteryOptimizationSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    fun openAlarmSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${context.packageName}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }
}
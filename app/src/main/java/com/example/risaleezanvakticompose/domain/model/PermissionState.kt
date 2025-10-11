package com.example.risaleezanvakticompose.domain.model

sealed class AppPermission {
    data object Location : AppPermission()
    data object Notification : AppPermission()
    data object BatteryOptimization : AppPermission()
}

data class PermissionState(
    val permission: AppPermission,
    val isGranted: Boolean,
    val shouldShowRationale: Boolean = false
)
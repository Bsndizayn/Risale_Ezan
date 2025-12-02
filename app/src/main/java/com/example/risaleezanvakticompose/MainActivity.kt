package com.example.risaleezanvakticompose

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.risaleezanvakticompose.presentation.navigation.RootNavigationGraph
import com.example.risaleezanvakticompose.presentation.navigation.Screen
import com.example.risaleezanvakticompose.service.MidnightAlarmReceiver
import com.example.risaleezanvakticompose.service.PrayerTimeAlarmReceiver
import com.example.risaleezanvakticompose.ui.theme.RisaleEzanVaktiComposeTheme
import com.example.risaleezanvakticompose.util.PermissionManager
import com.example.risaleezanvakticompose.util.WorkManagerHelper
import com.google.accompanist.systemuicontroller.rememberSystemUiController // YENİ EKLENDİ
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    @Inject
    lateinit var permissionManager: PermissionManager

    // Bildirim izni launcher'ı
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        android.util.Log.d("MainActivity", "Bildirim izni sonucu: $isGranted")
        permissionManager.markNotificationPermissionRequested()

        if (isGranted) {
            android.util.Log.d("MainActivity", "Bildirim izni verildi")
            // MainViewModel'deki alarm kurulumu otomatik yapılacak
        } else {
            android.util.Log.d("MainActivity", "Bildirim izni reddedildi")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handlePrayerNotificationIntent(intent)

        WorkManagerHelper.schedulePrayerTimesRefresh(this)
        MidnightAlarmReceiver.scheduleMidnightAlarm(this)

        // Bildirim iznini kontrol et ve iste
        requestNotificationPermissionIfNeeded()

        setContent {
            // YENİ EKLENDİ: Tüm uygulama genelinde Status Bar'ı (Çentiği) gizle
            val systemUiController = rememberSystemUiController()
            LaunchedEffect(systemUiController) {
                systemUiController.isStatusBarVisible = false
            }

            RisaleEzanVaktiComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var startDestination by remember { mutableStateOf<String?>(null) }

                    LaunchedEffect(Unit) {
                        val isCompleted = userPreferencesRepository.isOnboardingCompleted.first()
                        startDestination = if (isCompleted) {
                            Screen.Auth.Main.ROUTE
                        } else {
                            Screen.Auth.OnBoarding.ROUTE
                        }
                    }

                    startDestination?.let { destination ->
                        val navController = rememberNavController()

                        RootNavigationGraph(
                            navController = navController,
                            startDestination = destination,
                            onOnboardingComplete = {
                                lifecycleScope.launch {
                                    userPreferencesRepository.setOnboardingCompleted(true)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        // Android 13+ için bildirim izni gerekli
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!permissionManager.isNotificationPermissionGranted()) {
                android.util.Log.d("MainActivity", "Bildirim izni istenecek")
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                android.util.Log.d("MainActivity", "Bildirim izni zaten var")
            }
        } else {
            android.util.Log.d("MainActivity", "Android 13'ten küçük, bildirim izni gerekmiyor")
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handlePrayerNotificationIntent(intent)
    }

    private fun handlePrayerNotificationIntent(intent: Intent?) {
        intent?.let {
            if (it.getBooleanExtra("STOP_PRAYER_SOUND", false)) {
                android.util.Log.d("MainActivity", "Bildirimden gelindi, ses durduruluyor")
                PrayerTimeAlarmReceiver.stopSoundOnly()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Uygulama ön plana geldiğinde bildirim iznini tekrar kontrol et
        android.util.Log.d("MainActivity", "onResume - Bildirim izni kontrol ediliyor")
    }
}
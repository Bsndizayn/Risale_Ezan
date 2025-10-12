package com.example.risaleezanvakticompose

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.risaleezanvakticompose.presentation.navigation.RootNavigationGraph
import com.example.risaleezanvakticompose.presentation.navigation.Screen
import com.example.risaleezanvakticompose.service.MidnightAlarmReceiver
import com.example.risaleezanvakticompose.ui.theme.RisaleEzanVaktiComposeTheme
import com.example.risaleezanvakticompose.util.WorkManagerHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    // Bildirim izni isteme
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            android.util.Log.d("MainActivity", "Bildirim izni verildi")
        } else {
            android.util.Log.w("MainActivity", "Bildirim izni reddedildi")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Android 13+ için bildirim izni iste
        requestNotificationPermission()

        WorkManagerHelper.schedulePrayerTimesRefresh(this)
        MidnightAlarmReceiver.scheduleMidnightAlarm(this)

        setContent {
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

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    android.util.Log.d("MainActivity", "Bildirim izni zaten verilmiş")
                }
                else -> {
                    android.util.Log.d("MainActivity", "Bildirim izni isteniyor...")
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}
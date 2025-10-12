package com.example.risaleezanvakticompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.risaleezanvakticompose.presentation.navigation.RootNavigationGraph
import com.example.risaleezanvakticompose.presentation.navigation.Screen
import com.example.risaleezanvakticompose.service.MidnightAlarmReceiver
import com.example.risaleezanvakticompose.ui.theme.RisaleEzanVaktiComposeTheme
import com.example.risaleezanvakticompose.util.WorkManagerHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // WorkManager'ı başlat
        WorkManagerHelper.schedulePrayerTimesRefresh(this)

        // Gece yarısı alarmını kur
        MidnightAlarmReceiver.scheduleMidnightAlarm(this)

        setContent {
            RisaleEzanVaktiComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    RootNavigationGraph(
                        navController = navController,
                        startDestination = Screen.Auth.Permissions.ROUTE,
                        onOnboardingComplete = { }
                    )
                }
            }
        }
    }
}
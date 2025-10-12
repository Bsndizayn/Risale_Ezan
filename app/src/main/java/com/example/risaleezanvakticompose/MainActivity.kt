package com.example.risaleezanvakticompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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
}
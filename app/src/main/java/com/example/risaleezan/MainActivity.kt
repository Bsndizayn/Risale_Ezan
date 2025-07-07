package com.example.risaleezan

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Sistem bar renkleri
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.parseColor("#400606") // navigation bar'ı siyah yaptık

        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        bottomNavigationView.setupWithNavController(navController)
        setupBottomNavigationBehavior(bottomNavigationView)

        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView) { view, insets ->
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            view.setPadding(0, 0, 0, bottomInset)
            insets
        }
    }

    private fun setupBottomNavigationBehavior(bottomNav: BottomNavigationView) {
        bottomNav.setOnItemSelectedListener { item ->
            val navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
            val currentDestination = navController.currentDestination?.id

            when (item.itemId) {
                R.id.namazFragment -> {
                    if (currentDestination != R.id.namazFragment) {
                        navController.navigate(R.id.namazFragment)
                    }
                    true
                }
                R.id.kibleFragment -> {
                    if (currentDestination != R.id.kibleFragment) {
                        navController.navigate(R.id.kibleFragment)
                    }
                    true
                }
                R.id.risaleFragment -> {
                    if (currentDestination != R.id.risaleFragment) {
                        navController.navigate(R.id.risaleFragment)
                    }
                    true
                }
                R.id.ayarlarFragment -> {
                    if (currentDestination != R.id.ayarlarFragment) {
                        navController.navigate(R.id.ayarlarFragment)
                    }
                    true
                }
                else -> false
            }
        }
    }
}
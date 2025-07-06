package com.example.risaleezan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge display için
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        bottomNavigationView.setupWithNavController(navController)
        setupBottomNavigationBehavior(bottomNavigationView)
    }

    private fun setupBottomNavigationBehavior(bottomNav: BottomNavigationView) {
        bottomNav.setOnItemSelectedListener { item ->
            val navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

            when (item.itemId) {
                R.id.namazFragment -> {
                    navController.navigate(R.id.namazFragment)
                    true
                }
                R.id.kibleFragment -> {
                    navController.navigate(R.id.kibleFragment)
                    true
                }
                R.id.risaleFragment -> {
                    navController.navigate(R.id.risaleFragment)
                    true
                }
                R.id.ayarlarFragment -> {
                    navController.navigate(R.id.ayarlarFragment)
                    true
                }
                else -> false
            }
        }
    }
}
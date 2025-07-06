package com.example.risaleezan

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var windowInsetsController: WindowInsetsControllerCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge display için
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // WindowInsetsController'ı al
        windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        // Sistem barlarını gizle
        hideSystemBars()

        // Sistem barlarının rengini ayarla
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        bottomNavigationView.setupWithNavController(navController)
        setupBottomNavigationBehavior(bottomNavigationView)

        // Fragment container için sistem barı padding'i ekle
        val navHostFragmentView = findViewById<androidx.fragment.app.FragmentContainerView>(R.id.nav_host_fragment)
        ViewCompat.setOnApplyWindowInsetsListener(navHostFragmentView) { view, insets ->
            view.setPadding(0, 0, 0, 0)
            insets
        }

        // Bottom navigation için sistem barı padding'i ekle
        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView) { view, insets ->
            view.setPadding(0, 0, 0, 0)
            insets
        }

        // Root view'e tıklama listener'ı ekle
        setupSystemBarToggle()
    }

    private fun hideSystemBars() {
        windowInsetsController.apply {
            // Sistem barlarını gizle
            hide(WindowInsetsCompat.Type.systemBars())
            // Sistem barları davranışını ayarla
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun setupSystemBarToggle() {
        val rootView = findViewById<View>(android.R.id.content)

        // Sistem barlarının görünürlük durumunu takip et
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { _, insets ->
            val isVisible = insets.isVisible(WindowInsetsCompat.Type.systemBars())

            // Eğer sistem barları görünürse, 3 saniye sonra gizle
            if (isVisible) {
                rootView.postDelayed({
                    hideSystemBars()
                }, 3000) // 3 saniye sonra gizle
            }

            insets
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // Uygulama odaklandığında sistem barlarını gizle
            hideSystemBars()
        }
    }

    private fun setupBottomNavigationBehavior(bottomNav: BottomNavigationView) {
        bottomNav.setOnItemSelectedListener { item ->
            val navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

            // Mevcut destination'ı al
            val currentDestination = navController.currentDestination?.id

            // Eğer zaten o fragment'taysa, navigate etme
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
    }}
package com.example.risaleezanvakticompose.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.risaleezanvakticompose.R
import com.example.risaleezanvakticompose.presentation.screen.location.LocationSelectionScreen
import com.example.risaleezanvakticompose.presentation.screen.mainScreen.MainScreen
import com.example.risaleezanvakticompose.presentation.screen.onboardingScreen.OnboardingScreen
import com.example.risaleezanvakticompose.presentation.screen.profileScreen.ProfileScreen
import com.example.risaleezanvakticompose.presentation.screen.qibla.QiblaScreen
import com.example.risaleezanvakticompose.presentation.screen.settings.SettingsScreen
import com.example.risaleezanvakticompose.presentation.screen.tesbihat.TesbihatDetailScreen
import com.example.risaleezanvakticompose.presentation.screen.tesbihat.TesbihatListScreen
import com.example.risaleezanvakticompose.presentation.screen.tesbihat.TesbihatViewModel


@Composable
fun RootNavigationGraph(
    navController: NavHostController,
    startDestination: String,
    onOnboardingComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) }
    ) {
        composable(
            route = Screen.Auth.OnBoarding.ROUTE,
            enterTransition = { scaleIn(initialScale = 0.9f) }
        ) {
            OnboardingScreen(
                onFinishOnboarding = {
                    onOnboardingComplete()
                    navController.navigate(Screen.Auth.Main.ROUTE) {
                        popUpTo(Screen.Auth.OnBoarding.ROUTE) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onSkipOnboarding = {
                    onOnboardingComplete()
                    navController.navigate(Screen.Auth.Main.ROUTE) {
                        popUpTo(Screen.Auth.OnBoarding.ROUTE) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(route = Screen.Auth.Main.ROUTE) {
            MainScreenContent()
        }
    }
}

@Composable
fun MainScreenContent() {
    val mainNavController = rememberNavController()
    val navBackStackEntry by mainNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(modifier = Modifier.fillMaxSize()) {
        // Arka plan resmi en arkada, tüm ekranı kaplıyor
        Image(
            painter = painterResource(id = R.drawable.yeni_arkaplan),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent, // Scaffold'u transparan yaptık
            contentWindowInsets = WindowInsets.systemBars,
            bottomBar = {
                val shouldShowBottomBar = when (currentRoute) {
                    Screen.Main.Home.ROUTE,
                    Screen.Main.MyLibrary.ROUTE,
                    Screen.Main.Qibla.ROUTE,
                    Screen.Main.Profile.ROUTE -> true
                    else -> false
                }

                if (shouldShowBottomBar) {
                    RisaleBottomNavigationBar(
                        currentRoute = currentRoute,
                        onHomeClick = {
                            mainNavController.navigate(Screen.Main.Home.ROUTE) {
                                popUpTo(mainNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onLibraryClick = {
                            mainNavController.navigate(Screen.Main.MyLibrary.ROUTE) {
                                popUpTo(mainNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onQiblaClick = {
                            mainNavController.navigate(Screen.Main.Qibla.ROUTE) {
                                popUpTo(mainNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onProfileClick = {
                            mainNavController.navigate(Screen.Main.Profile.ROUTE) {
                                popUpTo(mainNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            MainScreenNavHost(
                navController = mainNavController,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun RisaleBottomNavigationBar(
    currentRoute: String?,
    onHomeClick: () -> Unit,
    onLibraryClick: () -> Unit,
    onQiblaClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ),
        color = Color(0xFF5C1A1A).copy(alpha = 0.95f), // Koyu kırmızı-kahverengi ton
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            contentColor = Color(0xFFF5E6D3), // Açık krem/bej renk
            tonalElevation = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (currentRoute == Screen.Main.Home.ROUTE)
                            Icons.Filled.Home
                        else
                            Icons.Outlined.Home,
                        contentDescription = "Ana Sayfa",
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = null, // Label'ı kaldırdık, daha minimal
                selected = currentRoute == Screen.Main.Home.ROUTE,
                onClick = onHomeClick,
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFFFD700), // Altın sarısı
                    selectedTextColor = Color(0xFFFFD700),
                    indicatorColor = Color(0xFF8B3A3A).copy(alpha = 0.3f), // Hafif kırmızı indicator
                    unselectedIconColor = Color(0xFFF5E6D3).copy(alpha = 0.6f),
                    unselectedTextColor = Color(0xFFF5E6D3).copy(alpha = 0.6f)
                )
            )

            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(
                            id = if (currentRoute == Screen.Main.MyLibrary.ROUTE)
                                R.drawable.ic_tasbih_filled
                            else
                                R.drawable.ic_tasbih_outlined
                        ),
                        contentDescription = "Tesbihatlar",
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = null,
                selected = currentRoute == Screen.Main.MyLibrary.ROUTE,
                onClick = onLibraryClick,
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFFFD700),
                    selectedTextColor = Color(0xFFFFD700),
                    indicatorColor = Color(0xFF8B3A3A).copy(alpha = 0.3f),
                    unselectedIconColor = Color(0xFFF5E6D3).copy(alpha = 0.6f),
                    unselectedTextColor = Color(0xFFF5E6D3).copy(alpha = 0.6f)
                )
            )

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (currentRoute == Screen.Main.Qibla.ROUTE)
                            Icons.Filled.Explore
                        else
                            Icons.Outlined.Explore,
                        contentDescription = "Kıble",
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = null,
                selected = currentRoute == Screen.Main.Qibla.ROUTE,
                onClick = onQiblaClick,
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFFFD700),
                    selectedTextColor = Color(0xFFFFD700),
                    indicatorColor = Color(0xFF8B3A3A).copy(alpha = 0.3f),
                    unselectedIconColor = Color(0xFFF5E6D3).copy(alpha = 0.6f),
                    unselectedTextColor = Color(0xFFF5E6D3).copy(alpha = 0.6f)
                )
            )

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (currentRoute == Screen.Main.Profile.ROUTE)
                            Icons.Filled.Person
                        else
                            Icons.Outlined.Person,
                        contentDescription = "Profil",
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = null,
                selected = currentRoute == Screen.Main.Profile.ROUTE,
                onClick = onProfileClick,
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFFFD700),
                    selectedTextColor = Color(0xFFFFD700),
                    indicatorColor = Color(0xFF8B3A3A).copy(alpha = 0.3f),
                    unselectedIconColor = Color(0xFFF5E6D3).copy(alpha = 0.6f),
                    unselectedTextColor = Color(0xFFF5E6D3).copy(alpha = 0.6f)
                )
            )
        }
    }
}

@Composable
fun MainScreenNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.Home.ROUTE,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(200)) },
        exitTransition = { fadeOut(animationSpec = tween(200)) }
    ) {
        composable(
            route = Screen.Main.Home.ROUTE,
            enterTransition = { scaleIn(initialScale = 0.95f, animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            MainScreen(
                onProfileClick = {
                    navController.navigate(Screen.Main.Profile.ROUTE) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onLocationClick = {
                    navController.navigate(Screen.Main.LocationSelection.ROUTE)
                }
            )
        }

        composable(
            route = Screen.Main.Profile.ROUTE,
            enterTransition = { scaleIn(initialScale = 0.95f) },
            exitTransition = { fadeOut() }
        ) {
            ProfileScreen(
                onLogout = { },
                onSaveSettings = { settings -> },
                onNavigateToSettings = {
                    navController.navigate(Screen.Main.Settings.ROUTE)
                }
            )
        }

        composable(route = Screen.Main.MyLibrary.ROUTE) {
            TesbihatListScreen(
                onCategoryClick = { category ->
                    val route = Screen.Detail.TesbihatDetail.createRoute(category.name)
                    navController.navigate(route)
                },
                onBackClick = { }
            )
        }

        composable(
            route = Screen.Main.Qibla.ROUTE,
            enterTransition = { scaleIn(initialScale = 0.95f, animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            QiblaScreen(navController = navController)
        }

        composable(
            route = Screen.Main.LocationSelection.ROUTE,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(300)
                )
            }
        ) {
            LocationSelectionScreen(navController = navController)
        }

        composable(
            route = Screen.Main.Settings.ROUTE,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                )
            }
        ) {
            SettingsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Detail.TesbihatDetail.ROUTE,
            arguments = listOf(
                navArgument(Screen.Detail.TesbihatDetail.ARG_CATEGORY_NAME) {
                    type = NavType.StringType
                    nullable = false
                }
            ),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                )
            }
        ) { backStackEntry ->
            val categoryName = Screen.Detail.TesbihatDetail.getCategoryName(backStackEntry)
            val category = com.example.risaleezanvakticompose.domain.model.TesbihatCategory.valueOf(
                categoryName
            )

            val viewModel: TesbihatViewModel = hiltViewModel()

            LaunchedEffect(category) {
                viewModel.selectCategory(category)
            }

            TesbihatDetailScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }
    }
}
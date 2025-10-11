package com.example.risaleezanvakticompose.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.risaleezanvakticompose.presentation.screen.location.LocationSelectionScreen
import com.example.risaleezanvakticompose.presentation.screen.mainScreen.MainScreen
import com.example.risaleezanvakticompose.presentation.screen.onboardingScreen.OnboardingScreen
import com.example.risaleezanvakticompose.presentation.screen.permissions.PermissionsScreen
import com.example.risaleezanvakticompose.presentation.screen.profileScreen.ProfileScreen
import com.example.risaleezanvakticompose.presentation.screen.readBook.Kulliyat
import com.example.risaleezanvakticompose.presentation.screen.readBook.NoteScreen
import com.example.risaleezanvakticompose.presentation.screen.readBook.ReadingScreen


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
            route = Screen.Auth.Permissions.ROUTE,
            enterTransition = { scaleIn(initialScale = 0.9f) }
        ) {
            PermissionsScreen(
                onAllPermissionsGranted = {
                    // İzinler verildi, Main ekranına git
                    navController.navigate(Screen.Auth.Main.ROUTE) {
                        popUpTo(Screen.Auth.Permissions.ROUTE) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onSkip = {
                    // Atla, direkt Main ekranına git
                    navController.navigate(Screen.Auth.Main.ROUTE) {
                        popUpTo(Screen.Auth.Permissions.ROUTE) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Onboarding ekranı
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

        // Ana ekran - içinde nested navigation var
        composable(route = Screen.Auth.Main.ROUTE) {
            MainScreenContent()
        }
    }
}

@Composable
fun MainScreenContent() {
    /**
     * İç navigation için ayrı NavController
     *
     * rememberNavController Composition'da NavController yaratıyor ve
     * configuration change'lerde korunmasını sağlıyor.
     *
     * Bu controller root controller'dan tamamen bağımsız.
     * Kendi back stack'i var, kendi lifecycle'ı var.
     * Root controller'a hiç dokunmuyor, touch etmiyor.
     *
     * Bu separation sayesinde:
     * • Logout yapınca sadece root controller'ı resetliyoruz
     * • Main navigation karmaşık hale gelince root etkilenmiyor
     * • Her NavHost bağımsız test edilebiliyor
     */
    val mainNavController = rememberNavController()

    /**
     * Current destination tracking
     *
     * currentBackStackEntryAsState() NavController'ın current entry'sini
     * Compose State olarak dönüştürüyor.
     *
     * ┌─ STATE OBSERVATION ─────────────────────────────────────────────────┐
     * │                                                                      │
     * │ NavController observable değil ama currentBackStackEntry property'si│
     * │ StateFlow. currentBackStackEntryAsState() bu Flow'u Compose State'e│
     * │ çeviriyor.                                                           │
     * │                                                                      │
     * │ Her navigation işleminde:                                           │
     * │ 1. NavController back stack'i güncelliyor                           │
     * │ 2. currentBackStackEntry StateFlow'u emit ediyor                    │
     * │ 3. Compose bu değişikliği görüyor                                   │
     * │ 4. Bu state'i kullanan composable'lar recompose oluyor             │
     * │                                                                      │
     * │ Bu sayede bottom navigation'daki selected state otomatik            │
     * │ güncelleniyor, manuel state management gereksiz.                    │
     * │                                                                      │
     * └──────────────────────────────────────────────────────────────────────┘
     */
    val navBackStackEntry by mainNavController.currentBackStackEntryAsState()

    /**
     * Current route extraction
     *
     * NavDestination'dan route string'i alıyoruz.
     * Null olabilir çünkü destination henüz set edilmemiş olabilir
     * (ilk composition'da).
     */
    val currentRoute = navBackStackEntry?.destination?.route

    /**
     * Scaffold - Material Design Layout Structure
     *
     * Scaffold Material 3'ün layout template'i. Şunları sağlıyor:
     * • TopAppBar slot
     * • BottomBar slot
     * • FloatingActionButton slot
     * • Drawer slot
     * • Content slot (lambda)
     * • Snackbar host
     *
     * Scaffold otomatik padding hesaplıyor, bottom bar ile content
     * overlap etmiyor. Content lambda PaddingValues alıyor,
     * bu değerleri content'e apply etmemiz gerekiyor.
     */
    Scaffold(
        /**
         * Bottom bar slot
         *
         * Bu slot'a composable geçiyoruz, persistent olarak kalıyor.
         * Yani navigation değişse bile bottom bar yerinde duruyor.
         *
         * Conditional rendering yapıyoruz: sadece main screen'lerde
         * bottom bar gösteriyoruz, detail screen'lerde gizliyoruz.
         */
        bottomBar = {
            /**
             * Bottom bar visibility logic
             *
             * Main screen'lerde (Home, Profile, MyLibrary) göster.
             * Detail screen'lerde (BookDetail, Quiz vb.) gizle.
             *
             * When expression kullanıyoruz, exhaustive olmasına gerek yok
             * çünkü default case (else) var.
             */
            val shouldShowBottomBar = when (currentRoute) {
                Screen.Main.Home.ROUTE,
                Screen.Main.Profile.ROUTE,
                Screen.Main.MyLibrary.ROUTE -> true

                else -> false
            }

            /**
             * Conditional composition
             *
             * If kullanarak composable conditionally render ediyoruz.
             * False olduğunda NavigationBar hiç compose edilmiyor,
             * memory'de bile yok. Bu performans için önemli.
             *
             * Alternatif: AnimatedVisibility ile animasyonlu göster/gizle
             * ama bottom bar için gerekli değil, instant toggle yeterli.
             */
            if (shouldShowBottomBar) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    /**
                     * Navigation callback'leri
                     *
                     * Her tab için ayrı callback. Bu callback'lerde
                     * bottom navigation pattern'ini uyguluyoruz:
                     * • popUpTo start destination
                     * • saveState ve restoreState
                     * • launchSingleTop
                     */
                    onHomeClick = {
                        mainNavController.navigate(Screen.Main.Home.ROUTE) {
                            /**
                             * ┌─ BOTTOM NAVIGATION PATTERN ────────────────────┐
                             * │                                                 │
                             * │ Bottom navigation'da tab değişimi özel:        │
                             * │                                                 │
                             * │ popUpTo(startDestinationId):                   │
                             * │ • Start destination'a kadar pop et             │
                             * │ • Start destination'ı tut (inclusive = false)  │
                             * │ • Örnek: Home → Profile → Detail → Profile    │
                             * │   Stack: [Home, Profile] olur                  │
                             * │                                                 │
                             * │ saveState = true:                              │
                             * │ • Pop edilen ekranların state'ini kaydet       │
                             * │ • Scroll position, form data korunur           │
                             * │ • Memory efficient: sadece state, UI değil     │
                             * │                                                 │
                             * │ restoreState = true:                           │
                             * │ • Önceki state'i geri yükle                   │
                             * │ • Profile'dayken scroll yaptıysan, Home'a     │
                             * │   gidip tekrar Profile'a dönünce scroll       │
                             * │   pozisyonu korunuyor                          │
                             * │                                                 │
                             * │ launchSingleTop = true:                        │
                             * │ • Aynı destination zaten stack'te varsa        │
                             * │   yeni instance yaratma                        │
                             * │ • Home'dayken Home tab'ına tekrar tıklarsan   │
                             * │   hiçbir şey olmasın                           │
                             * │                                                 │
                             * └─────────────────────────────────────────────────┘
                             */
                            popUpTo(mainNavController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onLibraryClick = {
                        /**
                         * Aynı pattern, farklı destination
                         * Code duplication var ama bottom navigation için
                         * kabul edilebilir. Her tab için logic aynı.
                         */
                        mainNavController.navigate(Screen.Main.MyLibrary.ROUTE) {
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
        /**
         * Content area - NavHost burada yaşıyor
         *
         * paddingValues Scaffold'dan geliyor, bottom bar'ın yüksekliğini
         * içeriyor. Bu padding'i NavHost'a apply ediyoruz ki content
         * bottom bar'ın altında kalmasın.
         */
        MainScreenNavHost(
            navController = mainNavController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onHomeClick: () -> Unit,
    onLibraryClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Ana Sayfa"
                )
            },
            label = { Text("Ana Sayfa") },
            selected = currentRoute == Screen.Main.Home.ROUTE,
            onClick = onHomeClick
        )

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.LibraryBooks,
                    contentDescription = "Kütüphanem"
                )
            },
            label = { Text("Kütüphanem") },
            selected = currentRoute == Screen.Main.MyLibrary.ROUTE,
            onClick = onLibraryClick
        )

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profil"
                )
            },
            label = { Text("Profil") },
            selected = currentRoute == Screen.Main.Profile.ROUTE,
            onClick = onProfileClick
        )
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
            enterTransition = {
                scaleIn(initialScale = 0.95f, animationSpec = tween(300))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            }
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
                onSaveSettings = { settings -> }
            )
        }

        composable(route = Screen.Main.MyLibrary.ROUTE) {
            Kulliyat(
                onBookClick = { bookId ->
                    val route = Screen.Detail.BookDetail.createRoute(bookId)
                    navController.navigate(route)
                },
                onProfileClick = {
                    navController.navigate(Screen.Main.Profile.ROUTE) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
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
            LocationSelectionScreen(
                onLocationSelected = { location ->
                    navController.navigate(Screen.Main.Home.ROUTE) {
                        popUpTo(Screen.Main.Home.ROUTE) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Detail.BookDetail.ROUTE,
            arguments = listOf(
                navArgument(Screen.Detail.BookDetail.ARG_BOOK_ID) {
                    type = NavType.StringType
                    nullable = false
                }
            ),
            enterTransition = {
                expandIn(
                    expandFrom = Alignment.Center,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                shrinkOut(
                    shrinkTowards = Alignment.Center,
                    animationSpec = tween(300)
                )
            }
        ) { backStackEntry ->
            val bookId = Screen.Detail.BookDetail.getBookID(backStackEntry)
            val rememberedBookId = remember(bookId) { bookId }

            ReadingScreen(
                bookID = rememberedBookId,
                onNotesClick = {
                    navController.navigate(
                        Screen.Detail.Note.createRoute(rememberedBookId)
                    )
                },
                onBackClick = {
                    navController.navigateUp()
                },
            )
        }

        composable(
            route = Screen.Detail.Note.ROUTE,
            arguments = listOf(
                navArgument(Screen.Detail.Note.ARG_BOOK_ID) {
                    type = NavType.StringType
                    nullable = false
                }
            ),
            enterTransition = {
                scaleIn(
                    initialScale = 0.9f,
                    animationSpec = tween(300)
                )
            },
            exitTransition = { fadeOut() }
        ) { backStackEntry ->
            val bookId = Screen.Detail.Note.getBookID(backStackEntry)

            NoteScreen(
                bookId = bookId,
                onNoteSaved = { },
                onNoteDeleted = { },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
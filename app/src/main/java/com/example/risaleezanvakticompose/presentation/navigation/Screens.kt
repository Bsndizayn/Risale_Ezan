package com.example.risaleezanvakticompose.presentation.navigation

import androidx.navigation.NavBackStackEntry

sealed class Screen(val route: String) {

    sealed class Auth(route: String) : Screen(route) {
        object OnBoarding : Auth("onboarding") {
            const val ROUTE = "onboarding"
        }

        object Main : Auth("main") {
            const val ROUTE = "main"
        }
    }

    sealed class Main(route: String) : Screen(route) {
        object Home : Main("home") {
            const val ROUTE = "home"
        }

        object MyLibrary : Main("my_library") {
            const val ROUTE = "my_library"
        }

        object Qibla : Main("qibla") {
            const val ROUTE = "qibla"
        }

        object Profile : Main("profile") {
            const val ROUTE = "profile"
        }

        object LocationSelection : Main("location_selection") {
            const val ROUTE = "location_selection"
        }

        object Settings : Main("settings") {
            const val ROUTE = "settings"
        }
    }

    sealed class Detail(route: String) : Screen(route) {
        object TesbihatSection : Detail("tesbihat_section/{categoryName}") {
            const val ROUTE = "tesbihat_section/{categoryName}"
            const val ARG_CATEGORY_NAME = "categoryName"

            fun createRoute(categoryName: String) = "tesbihat_section/$categoryName"

            fun getCategoryName(backStackEntry: NavBackStackEntry): String {
                return backStackEntry.arguments?.getString(ARG_CATEGORY_NAME) ?: ""
            }
        }

        object TesbihatDetail : Detail("tesbihat_detail/{categoryName}/{scrollId}") {
            const val ROUTE = "tesbihat_detail/{categoryName}/{scrollId}"
            const val ARG_CATEGORY_NAME = "categoryName"
            const val ARG_SCROLL_ID = "scrollId"

            fun createRoute(categoryName: String, scrollId: String) =
                "tesbihat_detail/$categoryName/$scrollId"

            fun getCategoryName(backStackEntry: NavBackStackEntry): String {
                return backStackEntry.arguments?.getString(ARG_CATEGORY_NAME) ?: ""
            }

            fun getScrollId(backStackEntry: NavBackStackEntry): String {
                return backStackEntry.arguments?.getString(ARG_SCROLL_ID) ?: ""
            }
        }
    }
}
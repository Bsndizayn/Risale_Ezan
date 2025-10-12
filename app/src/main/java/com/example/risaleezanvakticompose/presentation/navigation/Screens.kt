package com.example.risaleezanvakticompose.presentation.navigation

import androidx.navigation.NavBackStackEntry
import java.net.URLDecoder
import java.net.URLEncoder

sealed class Screen {
    abstract val route: String

    sealed class Auth : Screen() {
        data object OnBoarding : Auth() {
            override val route = "onboarding"
            const val ROUTE = "onboarding"
        }

        data object Permissions : Auth() {
            override val route = "permissions"
            const val ROUTE = "permissions"
        }

        data object Main : Auth() {
            override val route = "main"
            const val ROUTE = "main"
        }
    }

    sealed class Main : Screen() {
        data object Home : Main() {
            override val route = "home"
            const val ROUTE = "home"
        }

        data object Profile : Main() {
            override val route = "profile"
            const val ROUTE = "profile"
        }

        data object MyLibrary : Main() {
            override val route = "my_library"
            const val ROUTE = "my_library"
        }

        data object Qibla : Main() {
            override val route = "qibla"
            const val ROUTE = "qibla"
        }

        data object LocationSelection : Main() {
            override val route = "location_selection"
            const val ROUTE = "location_selection"
        }

        data object Settings : Main() {
            override val route = "settings"
            const val ROUTE = "settings"
        }
    }

    sealed class Detail : Screen() {

        data class TesbihatDetail(val categoryName: String = "") : Detail() {
            override val route = "tesbihat_detail/{categoryName}"

            companion object {
                const val ROUTE = "tesbihat_detail/{categoryName}"
                const val ARG_CATEGORY_NAME = "categoryName"

                fun createRoute(categoryName: String): String {
                    return "tesbihat_detail/$categoryName"
                }

                fun getCategoryName(entry: androidx.navigation.NavBackStackEntry): String {
                    return entry.arguments?.getString(ARG_CATEGORY_NAME)
                        ?: throw IllegalStateException("Category name required")
                }
            }
        }

        data class Note(val bookID: String = "") : Detail() {
            override val route = "note/{bookID}"

            companion object {
                const val ROUTE = "note/{bookID}"
                const val ARG_BOOK_ID = "bookID"

                fun createRoute(bookID: String): String {
                    require(bookID.isNotEmpty()) {
                        "Book ID cannot be empty for Note navigation"
                    }
                    return "note/$bookID"
                }

                fun getBookID(entry: NavBackStackEntry): String {
                    return entry.arguments?.getString(ARG_BOOK_ID)
                        ?: throw IllegalStateException(
                            "Book ID is required for Note screen"
                        )
                }
            }
        }
    }



}
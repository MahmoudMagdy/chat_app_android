package com.android.chatapp.feature_authentication.presentation.util

enum class Screen(val route: String) {
    LOGIN("login/"),
    RESET_PASSWORD("reset-password/"),
    USER_INFO("user-info/");

/*
    companion object {
        fun fromRoute(route: String?): AuthScreen =
            when (route) {
                LOGIN.route -> LOGIN
                RESET_PASSWORD.route -> RESET_PASSWORD
                USER_INFO.route -> USER_INFO
                null -> LOGIN
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
    }
*/
}
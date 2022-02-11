package com.android.chatapp.feature_gallery.presentation.util

enum class Screen(val route: String) {
    FOLDER_LIST("folder_list/"),
    MEDIA_LIST("media_list/"),
    CROPPER("cropper/");

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
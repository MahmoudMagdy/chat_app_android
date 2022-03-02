package com.android.chatapp.feature_authentication.data.provider.token.util

import com.android.chatapp.TokenPreferences


internal const val TOKEN_STORE_FILE_NAME = "token_prefs.pb"

val TokenPreferences.empty: Boolean get() = id == 0L || accessToken.isBlank() || refreshToken.isBlank()
val TokenPreferences.takeIfNotEmpty get() = takeIf { !empty }
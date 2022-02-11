package com.android.chatapp.core.domain.user_case

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CheckConnection @Inject constructor(
    @ApplicationContext private val context: Context
) {

    @Suppress("DEPRECATION")
    operator fun invoke() =
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getNetworkCapabilities(activeNetwork)?.apply {
                    return@run when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        else -> false
                    }
                }
            } else {
                activeNetworkInfo?.apply {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        return@run true
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        return@run true
                    }
                }
            }
            return@run false
        }

    /*
        if (authManager.isLoggedIn) {
            val uid = authManager.uid
            if (uid != null && !authManager.isAnonymous)
                repository.setUserOffline(uid)
        }
        //TODO: If Uninstall the app not remove anonymous we should delete it
    */

}


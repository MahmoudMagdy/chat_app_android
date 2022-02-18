package com.android.chatapp.core.presentation.service

import android.app.Service
import com.android.chatapp.core.presentation.util.CloseableCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

abstract class CoroutineService : Service() {
    protected val serviceScope = CloseableCoroutineScope()


    abstract suspend fun clear()
    final override fun onDestroy() {
        runBlocking { serviceScope.launch { clear() }.join() }
        serviceScope.close()
        super.onDestroy()
    }
}
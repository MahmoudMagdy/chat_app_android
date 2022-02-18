package com.android.chatapp.core.presentation.util

import kotlinx.coroutines.*
import java.io.Closeable
import java.util.*
import kotlin.coroutines.CoroutineContext

class CloseableCoroutineScope(
    context: CoroutineContext = SupervisorJob() + Dispatchers.IO + coroutineName
) :
    Closeable, CoroutineScope {
    override val coroutineContext: CoroutineContext = context

    override fun close() {
        coroutineContext.cancel()
    }
}

private const val COROUTINE_BASE_NAME = "service"
private val coroutineName = CoroutineName("${COROUTINE_BASE_NAME}_${UUID.randomUUID()}")
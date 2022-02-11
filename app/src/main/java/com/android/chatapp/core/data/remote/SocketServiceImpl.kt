package com.android.chatapp.core.data.remote

import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KType

abstract class SocketServiceImpl<in REQUEST, out RESPONSE>(
    private val client: HttpClient,
    private val serializer: Json,
    private val requestType: KType,
    private val responseType: KType
) : SocketService<RESPONSE> {
    private var socket: DefaultClientWebSocketSession? = null
    override val connected get() = socket != null
    protected suspend fun connect(url: String): DefaultClientWebSocketSession =
        client.webSocketSession { url(url) }
            .also { socket -> this.socket = socket }

    override suspend fun disconnect(block: (CloseReason?) -> Unit) {
        val socket = socket
        if (socket != null)
            block(socket.closeReason.await())

    }

    protected suspend fun _send(item: REQUEST) =
        socket!!.send(
            Frame.Text(
                serializer.encodeToString(
                    serializer.serializersModule.serializer(requestType),
                    item
                )
            )
        )

    @Suppress("UNCHECKED_CAST")
    override fun receive(): Flow<RESPONSE> = socket!!.incoming
        .receiveAsFlow()
        .filter { it is Frame.Text }
        .map {
            val json = (it as Frame.Text).readText()
            return@map serializer.decodeFromString(
                serializer.serializersModule.serializer(responseType),
                json
            ) as RESPONSE
        }

    override suspend fun close() {
        socket?.close()
        socket = null
    }
}
package com.android.chatapp.core.data.remote

import io.ktor.client.statement.*
import kotlinx.serialization.json.JsonObject
import java.io.InputStream

interface StorageService {
    suspend fun upload(
        uploadUrl: String,
        fields: JsonObject,
        inputStream: InputStream,
        contentType: String
    ): HttpResponse
}
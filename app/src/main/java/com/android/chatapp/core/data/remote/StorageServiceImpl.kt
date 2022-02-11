package com.android.chatapp.core.data.remote

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import java.io.InputStream

class StorageServiceImpl(private val client: HttpClient) : StorageService {

    override suspend fun upload(
        uploadUrl: String,
        fields: JsonObject,
        inputStream: InputStream,
        contentType: String
    ): HttpResponse =
        client.submitFormWithBinaryData(formData = formData {
            fields.forEach { field ->
                append(field.key, Json.decodeFromJsonElement<String>(field.value))
            }
            append("file", inputStream.readBytes())
        }) {
            url(uploadUrl)
            onUpload { bytesSentTotal, contentLength -> }
        }
}
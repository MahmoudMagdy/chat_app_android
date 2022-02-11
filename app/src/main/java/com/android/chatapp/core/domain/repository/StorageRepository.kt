package com.android.chatapp.core.domain.repository

import com.android.chatapp.core.data.util.ApiException
import com.android.chatapp.core.data.util.Resource
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonObject
import java.io.InputStream

interface StorageRepository {
    fun upload(
        url: String,
        fields: JsonObject,
        inputStream: InputStream,
        contentType: String
    ): Flow<Resource<HttpResponse, ApiException>>
}
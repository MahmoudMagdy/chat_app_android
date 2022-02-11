package com.android.chatapp.core.data.repository

import com.android.chatapp.core.data.remote.StorageService
import com.android.chatapp.core.data.util.ApiException
import com.android.chatapp.core.domain.repository.StorageRepository
import com.android.chatapp.core.data.util.Resource
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.JsonObject
import java.io.InputStream

class StorageRepositoryImpl constructor(
    private val service: StorageService
) : StorageRepository {

    override fun upload(
        url: String,
        fields: JsonObject,
        inputStream: InputStream,
        contentType: String
    ): Flow<Resource<HttpResponse, ApiException>> = flow {
        try {
            emit(Resource.Success(service.upload(url, fields, inputStream, contentType)))
        } catch (throwable: Throwable) {
            emit(Resource.Error(throwable))
        }
    }
}
package com.android.chatapp.feature_authentication.domain.user_case

import com.android.chatapp.core.data.util.ApiException
import com.android.chatapp.core.domain.repository.StorageRepository
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_authentication.data.remote.dto.ProfileUploadUrlResponse
import com.android.chatapp.feature_gallery.domain.model.LocalMedia
import com.android.chatapp.feature_gallery.domain.repository.LocalStorageRepository
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UploadProfilePhoto @Inject constructor(
    private val storage: StorageRepository,
    private val localStorage: LocalStorageRepository
) {
    operator fun invoke(
        urlResponse: ProfileUploadUrlResponse,
        image: LocalMedia
    ): Flow<Resource<HttpResponse, ApiException>> {
        val inputStream = localStorage.openInputStream(image.uri)!!
        return storage.upload(urlResponse.url, urlResponse.fields, inputStream, image.mimeType)
    }
}


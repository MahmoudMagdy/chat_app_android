package com.android.chatapp.feature_authentication.domain.user_case

import com.android.chatapp.feature_authentication.data.remote.dto.CreateProfileRequest
import com.android.chatapp.feature_authentication.data.remote.dto.ProfileMediaRequest
import com.android.chatapp.feature_authentication.data.remote.dto.ProfileUploadUrlResponse
import com.android.chatapp.feature_authentication.domain.model.ProfileMediaType
import com.android.chatapp.feature_gallery.domain.model.LocalMedia
import javax.inject.Inject

class AttachPhotoToRequest @Inject constructor() {
    operator fun invoke(
        urlResponse: ProfileUploadUrlResponse,
        profileRequest: CreateProfileRequest,
        image: LocalMedia
    ): CreateProfileRequest {
        val imageRequest = ProfileMediaRequest(
            urlResponse.cloudPath,
            image.name,
            ProfileMediaType.IMAGE,
            image.extension,
            image.size
        )
        return profileRequest.copy(image = imageRequest)
    }
}


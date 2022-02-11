package com.android.chatapp.feature_authentication.domain.user_case

import android.graphics.Bitmap
import com.android.chatapp.core.domain.util.AppFilesUtil
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_gallery.data.repository.LocalStorageRepositoryImpl
import com.android.chatapp.feature_gallery.domain.model.LocalImage
import com.android.chatapp.feature_gallery.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class SaveProfilePhoto @Inject constructor(private val repository: LocalStorageRepository) {

    operator fun invoke(bitmap: Bitmap): Flow<Resource<LocalImage, Nothing>> =
        repository.insertImageBitmapAtLocation(
            name ="${IMAGE_NAME_PREFIX}_${AppFilesUtil.nameSuffix}",
            dir =  LocalStorageRepositoryImpl.DIRECTORY_CAMERA_PROFILE_PICTURES,
            bitmap = bitmap
        )


    companion object {
        private const val IMAGE_NAME_PREFIX = "profile_pic"
    }
}

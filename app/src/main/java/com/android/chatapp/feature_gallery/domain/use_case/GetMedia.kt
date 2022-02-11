package com.android.chatapp.feature_gallery.domain.use_case

import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_gallery.domain.model.LocalMedia
import com.android.chatapp.feature_gallery.domain.repository.LocalStorageRepository
import com.android.chatapp.feature_gallery.presentation.util.Gallery
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMedia @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(
        gallery: Gallery,
        bucketId: Long
    ): Flow<Resource<List<LocalMedia>, Nothing>> =
        when (gallery) {
            Gallery.IMAGE -> repository.getImagesByFolder(bucketId)
            Gallery.VIDEO -> repository.getVideosByFolder(bucketId)
        }
}


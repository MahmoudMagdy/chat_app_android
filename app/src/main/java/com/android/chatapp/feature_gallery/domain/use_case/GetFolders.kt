package com.android.chatapp.feature_gallery.domain.use_case

import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_gallery.domain.model.MediaFolder
import com.android.chatapp.feature_gallery.domain.repository.LocalStorageRepository
import com.android.chatapp.feature_gallery.presentation.util.Gallery
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFolders @Inject constructor(
        private val repository: LocalStorageRepository
) {
    operator fun invoke(gallery: Gallery): Flow<Resource<List<MediaFolder>, Nothing>> =
            when (gallery) {
                Gallery.IMAGE -> repository.getImageFolders()
                Gallery.VIDEO -> repository.getVideoFolders()
            }
}


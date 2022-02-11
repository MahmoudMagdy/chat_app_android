package com.android.chatapp.feature_gallery.presentation.media_list

import com.android.chatapp.feature_gallery.domain.model.LocalMedia

sealed class UiEvent {
    data class PostMedia(val key: String, val media: LocalMedia) : UiEvent()
    data class CropImage(val image: LocalMedia) : UiEvent()
}

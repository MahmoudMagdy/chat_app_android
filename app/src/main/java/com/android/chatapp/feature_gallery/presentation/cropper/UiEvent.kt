package com.android.chatapp.feature_gallery.presentation.cropper

import com.android.chatapp.feature_gallery.domain.model.LocalMedia

sealed class UiEvent {
    data class PostMedia(val key: String, val media: LocalMedia) : UiEvent()
    object CancelCrop : UiEvent()
}

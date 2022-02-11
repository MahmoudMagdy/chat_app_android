package com.android.chatapp.feature_gallery.presentation.cropper

import com.android.chatapp.feature_gallery.presentation.cropper.views.CropImageView

sealed class CropperEvent {
    data class CropCompleted(val result: CropImageView.CropResult) : CropperEvent()
    object OnCancelClicked : CropperEvent()
    object OnRotateClicked : CropperEvent()
    object OnDoneClicked : CropperEvent()
}

package com.android.chatapp.feature_gallery.presentation.cropper.components

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.android.chatapp.feature_gallery.presentation.cropper.views.CropImageView

const val CROP_TYPE_FIXED_SQUARE_ASPECT_RATIO = 0
const val CROP_TYPE_AUTO_ASPECT_RATIO = 1

/**
 * Remembers a CropImageView
 */
@Composable
fun rememberCropImageView(
    uri: Uri,
    type: Int,
    onCropComplete: (view: CropImageView, CropImageView.CropResult) -> Unit
): CropImageView {
    val context = LocalContext.current
    val cropImageView = remember(uri, type) {
        CropImageView(context).apply {
            setImageUriAsync(uri)
            if (type == CROP_TYPE_FIXED_SQUARE_ASPECT_RATIO) {
                setAspectRatio(1, 1)
                setFixedAspectRatio(true)
            }
            setOnCropImageCompleteListener(onCropComplete)
        }
    }
    return cropImageView
}


sealed class CropImageViewEvent {
    object Nothing : CropImageViewEvent()
    data class RotateImage(val degree: Int) : CropImageViewEvent()
    data class CompleteCrop(val saveUri: Uri) : CropImageViewEvent()
}


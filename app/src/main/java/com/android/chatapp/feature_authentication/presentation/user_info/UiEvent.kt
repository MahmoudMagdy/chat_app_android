package com.android.chatapp.feature_authentication.presentation.user_info

import com.android.chatapp.feature_gallery.presentation.cropper.components.CROP_TYPE_FIXED_SQUARE_ASPECT_RATIO
import com.android.chatapp.feature_gallery.presentation.util.GallerySettings

sealed class UiEvent {
    object LoginCompleted : UiEvent()
    data class LaunchImageGallery(
        val settings: GallerySettings = GallerySettings(
            false,
            CROP_TYPE_FIXED_SQUARE_ASPECT_RATIO
        )
    ) : UiEvent()

    data class RequestCameraPermissions(val permissions: List<String>) : UiEvent()
    object LaunchAppSettings : UiEvent()
    object LaunchCamera : UiEvent()
    object LaunchPrivacyPolicy : UiEvent()
    object LaunchTerms : UiEvent()
    object NotAuthenticated : UiEvent()
    object ProfileAlreadyExists : UiEvent()
    object PopBackStack : UiEvent()
}

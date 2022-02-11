package com.android.chatapp.feature_authentication.presentation.user_info

import android.graphics.Bitmap
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import com.android.chatapp.core.domain.util.PermissionResult
import com.android.chatapp.core.domain.util.permissionsResult
import com.android.chatapp.feature_gallery.domain.model.LocalMedia
import com.android.chatapp.feature_gallery.presentation.MediaGallery
import com.android.chatapp.feature_gallery.presentation.util.GallerySettings

const val EFFECT_KEY = true

@Composable
fun rememberGalleryLauncher(onEvent: (UserInfoEvent) -> Unit): ManagedActivityResultLauncher<GallerySettings, LocalMedia?> =
    rememberLauncherForActivityResult(contract = MediaGallery(),
        onResult = { image -> onEvent(UserInfoEvent.ImageItemChosen(image)) }
    )

@Composable
fun rememberCameraLauncher(onEvent: (UserInfoEvent) -> Unit): ManagedActivityResultLauncher<Void?, Bitmap?> =
    rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            onEvent(UserInfoEvent.ImageTaken(bitmap))
        }
    )


@Composable
fun rememberCameraPermissionsLauncher(onEvent: (List<PermissionResult>) -> Unit): ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>> =
    rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { onEvent(it.permissionsResult) }
    )




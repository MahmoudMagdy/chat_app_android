package com.android.chatapp.feature_gallery.presentation.cropper

import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Rotate90DegreesCcw
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.chatapp.R
import com.android.chatapp.core.domain.util.ERROR_TAG
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.feature_gallery.domain.model.LocalMedia
import com.android.chatapp.feature_gallery.presentation.cropper.components.CropImageViewEvent
import com.android.chatapp.feature_gallery.presentation.cropper.components.WhiteTextButton
import com.android.chatapp.feature_gallery.presentation.cropper.components.rememberCropImageView
import com.android.chatapp.feature_gallery.presentation.cropper.views.CropImageView
import com.android.chatapp.ui.theme.ChatAppTheme
import kotlinx.coroutines.flow.collect


private const val EFFECT_KEY = true

@Composable
fun CropperScreen(
    modifier: Modifier = Modifier,
    postMedia: (key: String, media: LocalMedia) -> Unit,
    popBackStack: () -> Unit,
    viewModel: CropperViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = EFFECT_KEY) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.PostMedia -> postMedia(event.key, event.media)
                UiEvent.CancelCrop -> popBackStack()
            }
        }
    }

    Column(
        modifier = modifier
            .background(color = Color.Black)
            .fillMaxSize()
    ) {
        CropImageView(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            event = viewModel.cropImageEvent,
            uri = viewModel.uri,
            type = viewModel.cropType,
            onCropComplete = { _, result ->
                viewModel.onEvent(CropperEvent.CropCompleted(result))
            }
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            WhiteTextButton(
                modifier = Modifier.align(Alignment.CenterStart),
                text = stringResource(id = R.string.image_cropper_cancel_title),
                onClick = { viewModel.onEvent(CropperEvent.OnCancelClicked) }
            )
            IconButton(
                modifier = Modifier
                    .padding(horizontal = MaterialTheme.spacing.medium)
                    .align(Alignment.Center),
                onClick = { viewModel.onEvent(CropperEvent.OnRotateClicked) },
            ) {
                Icon(
                    imageVector = Icons.Outlined.Rotate90DegreesCcw,
                    contentDescription = stringResource(id = R.string.image_cropper_rotate_img_desc),
                    tint = Color.White
                )
            }
            WhiteTextButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                text = stringResource(id = R.string.image_cropper_done_title),
                onClick = { viewModel.onEvent(CropperEvent.OnDoneClicked) }
            )
        }
    }
}

@Composable
fun CropImageView(
    modifier: Modifier = Modifier,
    event: CropImageViewEvent,
    uri: Uri,
    type: Int,
    onCropComplete: (view: CropImageView, CropImageView.CropResult) -> Unit
) {
    val cropImageView = rememberCropImageView(uri, type, onCropComplete)
    CropImageContainer(modifier = modifier, cropImageView = cropImageView, event)
}

@Composable
fun CropImageContainer(
    modifier: Modifier = Modifier,
    cropImageView: CropImageView,
    event: CropImageViewEvent
) {
    Log.e(ERROR_TAG, "$event")
    AndroidView(
        modifier = modifier,
        factory = { cropImageView },
        update = { cropView ->
            when (event) {
                is CropImageViewEvent.CompleteCrop -> {
                    cropView.getCroppedImageAsync()
                    cropView.saveCroppedImageAsync(event.saveUri)
                }
                is CropImageViewEvent.RotateImage -> cropView.rotateImage(event.degree)
                CropImageViewEvent.Nothing -> Unit
            }
        }
    )

}

@Preview(
    name = "CropperScreen(LightMode)",
    showBackground = true,
    device = Devices.PIXEL_2
)
@Preview(
    name = "CropperScreen(DarkMode)",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_2
)
@Composable
fun CropperScreenPreview() {
    ChatAppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            CropperScreen(
                postMedia = { _, _ -> },
                popBackStack = {}
            )
        }
    }
}

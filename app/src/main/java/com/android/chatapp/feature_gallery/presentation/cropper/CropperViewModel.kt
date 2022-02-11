package com.android.chatapp.feature_gallery.presentation.cropper

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.chatapp.feature_gallery.domain.model.LocalMedia
import com.android.chatapp.feature_gallery.domain.use_case.CropperUseCases
import com.android.chatapp.feature_gallery.presentation.GALLERY_SETTINGS
import com.android.chatapp.feature_gallery.presentation.IMAGE_ITEM
import com.android.chatapp.feature_gallery.presentation.GALLERY_RESULT
import com.android.chatapp.feature_gallery.presentation.cropper.components.CropImageViewEvent
import com.android.chatapp.feature_gallery.presentation.util.GallerySettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CropperViewModel @Inject constructor(
    private val cases: CropperUseCases,
    state: SavedStateHandle
) : ViewModel() {

    private val media = state.get<LocalMedia>(IMAGE_ITEM)!!
    val uri get() = media.uri
    private val settings: GallerySettings = state.get(GALLERY_SETTINGS)!!
    val cropType get() = settings.cropType

    var cropImageEvent by mutableStateOf<CropImageViewEvent>(CropImageViewEvent.Nothing)
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent get() = _uiEvent.receiveAsFlow()

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    fun onEvent(event: CropperEvent) {
        event.apply {
            when (this) {
                CropperEvent.OnDoneClicked -> completeCropping()
                CropperEvent.OnRotateClicked -> cropImageEvent = CropImageViewEvent.RotateImage(90)
                is CropperEvent.CropCompleted -> onCropCompleted(result.uri)
                CropperEvent.OnCancelClicked -> sendUiEvent(UiEvent.CancelCrop)
            }
        }
    }

    private fun onCropCompleted(uri: Uri?) {
        if (uri != null)
            sendUiEvent(UiEvent.PostMedia(GALLERY_RESULT, cases.createLocalImage(uri)))
        else sendUiEvent(UiEvent.CancelCrop)
    }

    private fun completeCropping() {
        cropImageEvent = CropImageViewEvent.CompleteCrop(cases.generateTempFileUri(media))
    }
}
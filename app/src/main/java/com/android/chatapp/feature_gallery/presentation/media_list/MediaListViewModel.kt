package com.android.chatapp.feature_gallery.presentation.media_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.chatapp.feature_gallery.domain.model.LocalImage
import com.android.chatapp.feature_gallery.domain.model.LocalMedia
import com.android.chatapp.feature_gallery.domain.model.MediaFolder
import com.android.chatapp.feature_gallery.domain.use_case.MediaListUseCases
import com.android.chatapp.feature_gallery.presentation.GALLERY_SETTINGS
import com.android.chatapp.feature_gallery.presentation.GALLERY_RESULT
import com.android.chatapp.feature_gallery.presentation.MEDIA_FOLDER
import com.android.chatapp.feature_gallery.presentation.util.GallerySettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaListViewModel @Inject constructor(
    cases: MediaListUseCases,
    state: SavedStateHandle
) : ViewModel() {

    private val folder = state.get<MediaFolder>(MEDIA_FOLDER)!!
    private val settings: GallerySettings = state.get(GALLERY_SETTINGS)!!
    var title by mutableStateOf(folder.name)
        private set
    var uiState = cases.run {
        resourceToUiState(
            resFlow = getMedia(settings.gallery, folder.bucketId),
            gallery = settings.gallery
        )
    }

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent get() = _uiEvent.receiveAsFlow()

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    fun onEvent(event: MediaListEvent) {
        event.apply {
            when (this) {
                is MediaListEvent.MediaChosen -> onMediaChosen(media)
            }
        }
    }

    private fun onMediaChosen(media: LocalMedia) {
        sendUiEvent(
            if (media is LocalImage) {
                if (settings.cropMedia) UiEvent.CropImage(media)
                else UiEvent.PostMedia(GALLERY_RESULT, media)
            } else UiEvent.PostMedia(GALLERY_RESULT, media)
        )
    }

}
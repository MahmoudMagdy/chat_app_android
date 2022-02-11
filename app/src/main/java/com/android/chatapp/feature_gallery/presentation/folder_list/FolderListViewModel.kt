package com.android.chatapp.feature_gallery.presentation.folder_list

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.android.chatapp.feature_gallery.domain.use_case.FolderListUseCases
import com.android.chatapp.feature_gallery.presentation.GALLERY_SETTINGS
import com.android.chatapp.feature_gallery.presentation.util.GallerySettings
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class FolderListViewModel @Inject constructor(
    cases: FolderListUseCases,
    @ApplicationContext context: Context,
    state: SavedStateHandle
) : ViewModel() {
    private val settings: GallerySettings = state.get(GALLERY_SETTINGS)!!
    var title by mutableStateOf(context.getString(settings.gallery.title))
        private set
    var uiState =
        cases.run {
            resourceToUiState(
                resFlow = getFolders(settings.gallery),
                gallery = settings.gallery
            )
        }
}
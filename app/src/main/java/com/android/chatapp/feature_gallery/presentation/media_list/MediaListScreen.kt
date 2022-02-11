package com.android.chatapp.feature_gallery.presentation.media_list

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.chatapp.R
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.feature_gallery.domain.model.LocalMedia
import com.android.chatapp.feature_gallery.presentation.components.EmptyContent
import com.android.chatapp.feature_gallery.presentation.components.ErrorContent
import com.android.chatapp.feature_gallery.presentation.components.LoadingContent
import com.android.chatapp.feature_gallery.presentation.components.TitleTopBar
import com.android.chatapp.feature_gallery.presentation.media_list.components.MediaItem
import com.android.chatapp.feature_gallery.presentation.util.Gallery
import com.android.chatapp.feature_gallery.presentation.util.UiState
import com.android.chatapp.ui.theme.ChatAppTheme
import kotlinx.coroutines.flow.collect

private const val GRID_CELLS_COUNT = 3
private const val EFFECT_KEY = true

@Composable
fun MediaListScreen(
    modifier: Modifier = Modifier,
    postMedia: (key: String, media: LocalMedia) -> Unit,
    cropImage: (LocalMedia) -> Unit,
    popBackStack: () -> Unit,
    viewModel: MediaListViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = EFFECT_KEY) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.CropImage -> cropImage(event.image)
                is UiEvent.PostMedia -> postMedia(event.key, event.media)
            }
        }
    }
    Scaffold(topBar = {
        TitleTopBar(title = viewModel.title, onBackClicked = popBackStack)
    }) {
        val paddingModifier = modifier.padding(paddingValues = it)
        val uiState = viewModel.uiState.collectAsState(initial = UiState.Loading)
        uiState.value.apply {
            when (this) {
                is UiState.Error -> ErrorContent(
                    modifier = paddingModifier,
                    title = R.string.folder_list_folders_empty_title,
                    content = R.string.folder_list_folders_empty_text
                )
                is UiState.Loaded ->
                    LoadedContent(
                        modifier = paddingModifier,
                        media = items,
                        gallery = gallery,
                        onItemClick = { media -> viewModel.onEvent(MediaListEvent.MediaChosen(media)) }
                    )
                UiState.Loading -> LoadingContent(modifier = paddingModifier)
                UiState.Empty -> EmptyContent(
                    modifier = paddingModifier,
                    text = stringResource(id = R.string.folder_list_folders_empty_text)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoadedContent(
    modifier: Modifier = Modifier,
    media: List<LocalMedia>,
    gallery: Gallery,
    onItemClick: (LocalMedia) -> Unit
) {
    Box(modifier = modifier) {
        LazyVerticalGrid(cells = GridCells.Fixed(GRID_CELLS_COUNT)) {
            items(media) { item ->
                MediaItem(item = item, gallery = gallery, onClick = onItemClick)
            }
        }
    }
}


@Preview(
    name = "MediaListScreen(LightMode)",
    showBackground = true,
    device = Devices.PIXEL_2
)
@Preview(
    name = "MediaListScreen(DarkMode)",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_2
)
@Composable
fun MediaListScreenPreview() {
    ChatAppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            MediaListScreen(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                postMedia = { _, _ -> },
                cropImage = {},
                popBackStack = {}
            )
        }
    }
}

package com.android.chatapp.feature_gallery.presentation.folder_list

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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.chatapp.R
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.feature_gallery.domain.model.MediaFolder
import com.android.chatapp.feature_gallery.presentation.components.EmptyContent
import com.android.chatapp.feature_gallery.presentation.components.ErrorContent
import com.android.chatapp.feature_gallery.presentation.components.LoadingContent
import com.android.chatapp.feature_gallery.presentation.components.TitleTopBar
import com.android.chatapp.feature_gallery.presentation.folder_list.components.FolderItem
import com.android.chatapp.feature_gallery.presentation.util.Gallery
import com.android.chatapp.feature_gallery.presentation.util.UiState
import com.android.chatapp.ui.theme.ChatAppTheme


private const val GRID_CELLS_COUNT = 3

@Composable
fun FolderListScreen(
    modifier: Modifier = Modifier,
    openFolder: (MediaFolder) -> Unit,
    cancel: () -> Unit,
    viewModel: FolderListViewModel = hiltViewModel()
) {
    Scaffold(topBar = {
        TitleTopBar(title = viewModel.title, onBackClicked = cancel)
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
                        folders = items,
                        gallery = gallery,
                        onItemClick = openFolder
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
    folders: List<MediaFolder>,
    gallery: Gallery,
    onItemClick: (MediaFolder) -> Unit
) {
    Box(modifier = modifier) {
        LazyVerticalGrid(cells = GridCells.Fixed(GRID_CELLS_COUNT)) {
            items(folders) { folder ->
                FolderItem(item = folder, gallery = gallery, onClick = onItemClick)
            }
        }
    }
}


@Preview(
    name = "FolderListScreen(LightMode)",
    showBackground = true,
    device = Devices.PIXEL_2
)
@Preview(
    name = "FolderListScreen(DarkMode)",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_2
)
@Composable
fun FolderListScreenPreview() {
    ChatAppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            FolderListScreen(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                openFolder = {},
                cancel = {}
            )
        }
    }
}

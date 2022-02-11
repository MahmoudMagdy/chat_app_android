package com.android.chatapp.feature_gallery.presentation.folder_list.components

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.android.chatapp.R
import com.android.chatapp.feature_gallery.domain.model.MediaFolder
import com.android.chatapp.feature_gallery.presentation.components.GalleryItem
import com.android.chatapp.feature_gallery.presentation.util.Gallery
import com.android.chatapp.ui.theme.ChatAppTheme

@Composable
fun FolderItem(
    modifier: Modifier = Modifier,
    item: MediaFolder,
    gallery: Gallery,
    onClick: (MediaFolder) -> Unit
) {
    GalleryItem(
        modifier = modifier,
        name = item.name,
        size = item.sizeStr(stringResource(id = R.string.folder_list_folder_size_desc)),
        uri = item.firstMediaUri,
        gallery = gallery,
        selected = false,
        onClick = { onClick(item) }
    ) {}
}


@Preview(
    name = "FolderItem(LightMode)",
    showBackground = true,
)
@Preview(
    name = "FolderItem(DarkMode)",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun FolderItemPreview() {
    ChatAppTheme {
        Surface {
            FolderItem(
                item = MediaFolder(
                    name = "Downloads",
                    bucketId = -1,
                    Uri.EMPTY
                ),
                gallery = Gallery.IMAGE,
                onClick = {}
            )
        }
    }
}
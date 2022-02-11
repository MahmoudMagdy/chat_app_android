package com.android.chatapp.feature_gallery.presentation.media_list.components

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.feature_gallery.domain.model.LocalMedia
import com.android.chatapp.feature_gallery.domain.model.LocalVideo
import com.android.chatapp.feature_gallery.presentation.components.GalleryItem
import com.android.chatapp.feature_gallery.presentation.components.VERTICAL_PADDING
import com.android.chatapp.feature_gallery.presentation.util.Gallery
import com.android.chatapp.ui.theme.ChatAppTheme

@Composable
fun MediaItem(
    modifier: Modifier = Modifier,
    item: LocalMedia,
    gallery: Gallery,
    onClick: (LocalMedia) -> Unit
) {
    GalleryItem(
        modifier = modifier,
        name = item.name,
        size = item.sizeStr,
        uri = item.uri,
        gallery = gallery,
        selected = item.selected,
        onClick = { onClick(item) }
    ) {
        if (item is LocalVideo)
            Text(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(all = VERTICAL_PADDING)
                    .background(
                        color = MaterialTheme.colors.onSurface,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(vertical = 1.dp, horizontal = MaterialTheme.spacing.extraSmall),
                text = item.durationStr,
                color = MaterialTheme.colors.surface,
                style = MaterialTheme.typography.body2.copy(fontSize = 12.sp)
            )
    }
}


@Preview(
    name = "MediaItem(LightMode)",
    showBackground = true,
)
@Preview(
    name = "MediaItem(DarkMode)",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun MediaItemPreview() {
    ChatAppTheme {
        Surface {
            MediaItem(
                item = LocalVideo(
                    duration = 240,
                    name = "image_thumbnail2022",
                    id = 10,
                    uri = Uri.EMPTY,
                    size = (1024 * 1024 * 6.3).toLong(),
                    mimeType = "image/jpg"
                ).apply { select },
                gallery = Gallery.IMAGE,
                onClick = {}
            )
        }
    }
}
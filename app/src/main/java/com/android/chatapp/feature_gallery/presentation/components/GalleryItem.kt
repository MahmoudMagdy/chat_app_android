package com.android.chatapp.feature_gallery.presentation.components

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.fetch.VideoFrameUriFetcher
import com.android.chatapp.R
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.feature_gallery.domain.model.LocalVideo
import com.android.chatapp.feature_gallery.presentation.util.Gallery
import com.android.chatapp.ui.theme.ChatAppTheme

@Composable
fun GalleryItem(
    modifier: Modifier = Modifier,
    name: String,
    size: String,
    uri: Uri,
    gallery: Gallery,
    selected: Boolean,
    onClick: () -> Unit,
    extraInfo: @Composable BoxScope.() -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (selected)
            MaterialTheme.colors.primary.copy(alpha = 0.9f, red = 0.7f, blue = 0.7f, green = 0.5f)
        else MaterialTheme.colors.surface
    )
    val context = LocalContext.current
    Box(
        modifier = modifier
            .background(bgColor)
            .padding(all = MaterialTheme.spacing.small)
    ) {
        Card(
            modifier = Modifier
                .height(120.dp)
                .clickable(onClick = onClick)
        ) {
            Box {
                Image(
                    painter = rememberImagePainter(
                        data = uri,
                        builder = {
                            crossfade(true)
                            if (gallery == Gallery.VIDEO)
                                fetcher(VideoFrameUriFetcher(context))
                        }
                    ),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = stringResource(id = R.string.images_list_image_thumbnail),
                    contentScale = ContentScale.Crop
                )
                extraInfo()
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(
                            horizontal = MaterialTheme.spacing.small,
                            vertical = VERTICAL_PADDING
                        )
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.subtitle2
                            .copy(
                                shadow = Shadow(
                                    color = Color.Black,
                                    offset = Offset(4f, 0f),
                                    blurRadius = 2f
                                )
                            ),
                        maxLines = 2,
                        color = Color.White
                    )
                    Text(
                        text = size,
                        style = MaterialTheme.typography.body2
                            .copy(
                                fontSize = 12.sp,
                                shadow = Shadow(
                                    color = Color.Black,
                                    offset = Offset(4f, 0f),
                                    blurRadius = 2f
                                )
                            ),
                        maxLines = 1,
                        color = Color.White
                    )
                }
            }
        }
    }

}

val VERTICAL_PADDING = 6.dp

@Preview(
    name = "GalleryItem(LightMode)",
    showBackground = true,
)
@Preview(
    name = "GalleryItem(DarkMode)",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun GalleryItemPreview() {
    ChatAppTheme {
        Surface {
            val item = LocalVideo(
                duration = 240,
                name = "image_thumbnail2022",
                id = 10,
                uri = Uri.EMPTY,
                size = (1024 * 1024 * 6.3).toLong(),
                mimeType = "image/jpg"
            ).apply { select }
            GalleryItem(
                name = item.name,
                size = item.sizeStr,
                uri = item.uri,
                gallery = Gallery.IMAGE,
                selected = item.selected,
                onClick = {}
            ) {}
        }
    }
}
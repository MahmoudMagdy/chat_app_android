package com.android.chatapp.feature_dialog.presentation.image_selector

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.android.chatapp.R
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.feature_dialog.presentation.image_selector.components.VerticalImageTextButton
import com.android.chatapp.ui.theme.ChatAppTheme
import java.util.*


class ImageSelectorDialogState(
    @StringRes val title: Int,
    @StringRes val actionTitle: Int = R.string.img_selector_cancel_title
) {
    companion object {
        val Saver: Saver<ImageSelectorDialogState, *> = listSaver(
            save = {
                it.run { listOf(title, actionTitle) }
            },
            restore = {
                ImageSelectorDialogState(
                    title = it[0],
                    actionTitle = it[1],
                )
            }
        )
    }
}

@Composable
fun rememberImageSelectorDialogState(
    @StringRes title: Int,
    @StringRes actionTitle: Int = R.string.img_selector_cancel_title,
) = rememberSaveable(title, actionTitle, saver = ImageSelectorDialogState.Saver) {
    ImageSelectorDialogState(title, actionTitle)
}


@Composable
fun ImageSelectorDialog(
    modifier: Modifier = Modifier,
    state: ImageSelectorDialogState,
    onEvent: (ImageSelectorEvent) -> Unit
) {
    Dialog(onDismissRequest = { onEvent(ImageSelectorEvent.DISMISS_REQUESTED) }) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colors.surface,
                    shape = RoundedCornerShape(8.dp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(all = MaterialTheme.spacing.small),
                text = stringResource(id = state.title),
                style = MaterialTheme.typography.h6
            )
            Divider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 28.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                VerticalImageTextButton(
                    image = R.drawable.img_selector_camera_src,
                    text = R.string.img_selector_camera_title,
                    imageDescription = R.string.img_selector_camera_desc,
                    color = Color.Blue,
                    onClick = { onEvent(ImageSelectorEvent.CAMERA_CLICKED) }
                )
                VerticalImageTextButton(
                    image = R.drawable.img_selector_gallery_src,
                    text = R.string.img_selector_gallery_title,
                    imageDescription = R.string.img_selector_gallery_desc,
                    color = Color.Cyan,
                    onClick = { onEvent(ImageSelectorEvent.GALLERY_CLICKED) }
                )
                VerticalImageTextButton(
                    image = R.drawable.img_selector_remove_src,
                    text = R.string.img_selector_remove_title,
                    imageDescription = R.string.img_selector_remove_desc,
                    color = Color.Red,
                    onClick = { onEvent(ImageSelectorEvent.REMOVE_CLICKED) }
                )
            }
            Divider()
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onEvent(ImageSelectorEvent.ACTION_CLICKED) }) {
                Text(text = stringResource(id = state.actionTitle).uppercase(Locale.getDefault()))
            }
        }
    }
}


@Preview(
    name = "ImageSelectorDialog(LightMode)",
    showBackground = true,
    device = Devices.PIXEL_2
)
@Preview(
    name = "ImageSelectorDialog(DarkMode)",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_2
)
@Composable
fun ImageSelectorDialogPreview() {
    ChatAppTheme {
        Surface {
            ImageSelectorDialog(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                state = rememberImageSelectorDialogState(title = R.string.img_selector_profile_title),
                onEvent = {}
            )
        }
    }
}
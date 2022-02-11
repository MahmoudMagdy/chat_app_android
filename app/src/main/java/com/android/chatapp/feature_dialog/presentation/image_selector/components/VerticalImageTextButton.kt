package com.android.chatapp.feature_dialog.presentation.image_selector.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.chatapp.R
import com.android.chatapp.ui.theme.ChatAppTheme
import java.util.*

@Composable
fun VerticalImageTextButton(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    @DrawableRes image: Int,
    @StringRes text: Int,
    @StringRes imageDescription: Int,
    onClick: () -> Unit
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(contentColor = Color.Unspecified)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = image),
                contentDescription = stringResource(id = imageDescription)
            )
            Text(
                text = stringResource(id = text).uppercase(Locale.getDefault()),
                style = MaterialTheme.typography.body2.copy(
                    letterSpacing = 0.4.sp
                ),
                color = color
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
fun VerticalImageTextButtonPreview() {
    ChatAppTheme {
        Surface {
            VerticalImageTextButton(
                image = R.drawable.img_selector_camera_src,
                text = R.string.img_selector_camera_title,
                imageDescription = R.string.img_selector_camera_desc,
                color = Color.Blue
            ) {
            }
        }
    }
}
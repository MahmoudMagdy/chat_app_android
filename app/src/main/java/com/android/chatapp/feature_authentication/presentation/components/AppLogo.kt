package com.android.chatapp.feature_authentication.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Face
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.chatapp.R
import com.android.chatapp.ui.theme.ChatAppTheme


@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    description: String = stringResource(id = R.string.app_logo_desc)
) {
    Image(
        painter = painterResource(id = R.drawable.ic_chat_app_logo),
        contentDescription = description,
        modifier = modifier.size(52.dp),
    )
}


@Preview(
    name = "AppLogo(LightMode)",
    showBackground = true,
)
@Preview(
    name = "AppLogo(DarkMode)",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun AppLogoPreview() {
    ChatAppTheme {
        Surface {
            AppLogo()
        }
    }
}
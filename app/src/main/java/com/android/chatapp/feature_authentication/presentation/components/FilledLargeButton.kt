package com.android.chatapp.feature_authentication.presentation.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.chatapp.ui.theme.ChatAppTheme


@Composable
fun FilledLargeButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .fillMaxWidth(),
        enabled = enabled,
        onClick = onClick
    ) {
        Text(text = text)
    }
}


@Preview(
    name = "FilledLargeButton(LightMode)",
    showBackground = true,
    widthDp = 480,
)
@Preview(
    name = "FilledLargeButton(DarkMode)",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES,
    widthDp = 480,
)
@Composable
fun AuthButtonPreview() {
    ChatAppTheme {
        Surface {
            FilledLargeButton(
                text = "Login",
            ) {}
        }
    }
}
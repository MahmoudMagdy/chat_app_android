package com.android.chatapp.feature_authentication.presentation.login.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.android.chatapp.ui.theme.ChatAppTheme


@Composable
fun ObviousTextButton(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.error,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
            .wrapContentSize()
    ) {
        Text(text = text, color = color)
    }
}


@Preview(
    name = "ObviousTextButton(LightMode)",
    showBackground = true,
)
@Preview(
    name = "ObviousTextButton(DarkMode)",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun ForgetPasswordPreview() {
    ChatAppTheme {
        Surface {
            ObviousTextButton(
                text = "Forget Password",
            ) {}
        }
    }
}
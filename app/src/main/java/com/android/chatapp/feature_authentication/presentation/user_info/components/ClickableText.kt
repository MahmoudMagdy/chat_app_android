package com.android.chatapp.feature_authentication.presentation.user_info.components

import android.content.res.Configuration
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.android.chatapp.R
import com.android.chatapp.ui.theme.ChatAppTheme


@Composable
fun ClickableText(
    modifier: Modifier = Modifier,
    text: Int,
    style: TextStyle,
    baseColor: Color = MaterialTheme.colors.primary,
    pressedColor: Color = MaterialTheme.colors.secondaryVariant,
    onClick: () -> Unit
) = ClickableText(
    modifier = modifier,
    text = stringResource(id = text),
    style = style,
    baseColor = baseColor,
    pressedColor = pressedColor,
    onClick = onClick
)


@Composable
fun ClickableText(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle,
    baseColor: Color = MaterialTheme.colors.primary,
    pressedColor: Color = MaterialTheme.colors.secondaryVariant,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val color = if (isPressed) pressedColor else baseColor
    Text(
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            onClick = onClick
        ),
        text = text,
        color = color,
        style = style
    )
}


@Preview(
    name = "ClickableText(LightMode)",
    showBackground = true,
)
@Preview(
    name = "ClickableText(DarkMode)",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun ClickableTextPreview() {
    ChatAppTheme {
        Surface {
            ClickableText(
                text = R.string.user_info_agreement_privacy_content,
                style = MaterialTheme.typography.h6,
                onClick = {}
            )
        }
    }
}
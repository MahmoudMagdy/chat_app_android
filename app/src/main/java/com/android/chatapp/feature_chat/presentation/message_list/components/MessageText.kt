package com.android.chatapp.feature_chat.presentation.message_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.android.chatapp.core.presentation.util.spacing

@Composable
fun MessageText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color,
    bgColor: Color,
    bgShape: Shape
) {
    Text(
        modifier = modifier
            .background(color = bgColor, shape = bgShape)
            .padding(
                vertical = MaterialTheme.spacing.small,
                horizontal = MaterialTheme.spacing.extraMedium
            ),
        text = text,
        style = MaterialTheme.typography.body1,
        color = color
    )
}

val MESSAGE_CORNER_RADIUS = 18.dp

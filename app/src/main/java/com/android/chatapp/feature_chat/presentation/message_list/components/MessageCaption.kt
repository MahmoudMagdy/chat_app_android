package com.android.chatapp.feature_chat.presentation.message_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.android.chatapp.core.presentation.util.spacing

@Composable
fun MessageCaption(
    modifier: Modifier = Modifier,
    caption: String,
    color: Color
) {
    Text(
        modifier = modifier
            .background(
                color = color,
                shape = RoundedCornerShape(size = MESSAGE_CAPTION_CORNER_RADIUS)
            )
            .padding(
                vertical = MESSAGE_CAPTION_PADDING_VERTICAL,
                horizontal = MaterialTheme.spacing.small
            ),
        text = caption,
        style = MaterialTheme.typography.caption,
        color = Color.DarkGray
    )
}

val MESSAGE_CAPTION_PADDING_VERTICAL = 2.dp
val MESSAGE_CAPTION_CORNER_RADIUS = 10.dp

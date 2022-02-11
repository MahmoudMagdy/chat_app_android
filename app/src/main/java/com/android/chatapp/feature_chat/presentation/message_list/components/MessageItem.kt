package com.android.chatapp.feature_chat.presentation.message_list.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.chatapp.core.presentation.util.readable
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.feature_chat.domain.model.MESSAGE_PREVIEW
import com.android.chatapp.feature_chat.domain.model.Message
import com.android.chatapp.ui.theme.Caption2Background
import com.android.chatapp.ui.theme.ChatAppTheme

@Composable
fun MessageItem(
    message: Message,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = MaterialTheme.spacing.small,
                bottom = MaterialTheme.spacing.small,
                end = MESSAGE_ITEM_PADDING_BEGAN,
                start = MESSAGE_ITEM_PADDING_END
            ),
        horizontalAlignment = Alignment.End
    ) {
        MessageText(
            text = message.content,
            color = Color.White,
            bgColor = MaterialTheme.colors.primary,
            bgShape = RoundedCornerShape(
                topStart = MESSAGE_CORNER_RADIUS,
                topEnd = MESSAGE_CORNER_RADIUS,
                bottomStart = MESSAGE_CORNER_RADIUS
            )
        )
        Row(Modifier.padding(top = MESSAGE_CAPTIONS_PADDING_TOP)) {
            MessageCaption(
                caption = message.created_at.readable,
                color = Caption2Background
            )
        }
    }
}


@Preview(
    name = "Light Mode",
    showBackground = true,
)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
fun MessageItemPreview() {
    ChatAppTheme {
        Surface {
            MessageItem(message = MESSAGE_PREVIEW)
        }
    }
}

val MESSAGE_ITEM_PADDING_BEGAN = 12.dp
val MESSAGE_ITEM_PADDING_END = 48.dp
val MESSAGE_CAPTIONS_PADDING_TOP = 6.dp

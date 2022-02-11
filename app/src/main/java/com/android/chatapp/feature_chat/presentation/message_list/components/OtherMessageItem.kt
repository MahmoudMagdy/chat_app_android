package com.android.chatapp.feature_chat.presentation.message_list.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.android.chatapp.feature_authentication.domain.model.PROFILE_PREVIEW
import com.android.chatapp.feature_authentication.domain.model.Profile
import com.android.chatapp.feature_chat.domain.model.MESSAGE_PREVIEW
import com.android.chatapp.feature_chat.domain.model.Message
import com.android.chatapp.feature_chat.presentation.components.ProfileImage
import com.android.chatapp.ui.theme.Caption3Background
import com.android.chatapp.ui.theme.ChatAppTheme

@Composable
fun OtherMessageItem(
    message: Message,
    profile: Profile,
    displayImage: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = MaterialTheme.spacing.small,
                bottom = MaterialTheme.spacing.small,
                start = MESSAGE_ITEM_PADDING_BEGAN,
                end = OTHER_MESSAGE_ITEM_PADDING_END
            )
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            if (displayImage)
                ProfileImage(
                    profile = profile,
                    size = OTHER_MESSAGE_PROFILE_IMAGE_SIZE,
                    shape = CircleShape
                )
            else
                Spacer(modifier = Modifier.width(OTHER_MESSAGE_PROFILE_IMAGE_SIZE))
            MessageText(
                modifier = Modifier.padding(start = MESSAGE_ITEM_PADDING_BEGAN),
                text = message.content,
                color = Color.Black,
                bgColor = Color.Gray,
                bgShape = RoundedCornerShape(
                    topStart = MESSAGE_CORNER_RADIUS,
                    topEnd = MESSAGE_CORNER_RADIUS,
                    bottomEnd = MESSAGE_CORNER_RADIUS
                )
            )
        }
        Row(Modifier.padding(top = MESSAGE_CAPTIONS_PADDING_TOP)) {
            Spacer(modifier = Modifier.width(OTHER_MESSAGE_PROFILE_IMAGE_SIZE))
            MessageCaption(
                modifier = Modifier.padding(start = MESSAGE_ITEM_PADDING_BEGAN),
                caption = message.created_at.readable,
                color = Caption3Background
            )
        }
    }
}

val OTHER_MESSAGE_PROFILE_IMAGE_SIZE = 44.dp
val OTHER_MESSAGE_ITEM_PADDING_END = 28.dp

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
fun OtherMessageItemPreview() {
    ChatAppTheme {
        Surface {
            OtherMessageItem(
                message = MESSAGE_PREVIEW,
                profile = PROFILE_PREVIEW,
                displayImage = true
            )
        }
    }
}

@Preview(
    name = "Light Mode (NoImage)",
    showBackground = true,
)
@Preview(
    name = "Dark Mode (NoImage)",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
fun OtherMessageItemNoImagePreview() {
    ChatAppTheme {
        Surface {
            OtherMessageItem(
                message = MESSAGE_PREVIEW,
                profile = PROFILE_PREVIEW,
                displayImage = false
            )
        }
    }
}

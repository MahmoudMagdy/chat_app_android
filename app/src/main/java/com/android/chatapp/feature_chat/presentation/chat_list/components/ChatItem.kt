package com.android.chatapp.feature_chat.presentation.chat_list.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.chatapp.R
import com.android.chatapp.core.presentation.util.readable
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.feature_authentication.domain.model.SessionState
import com.android.chatapp.feature_authentication.domain.model.name
import com.android.chatapp.feature_chat.domain.model.CHAT_PREVIEW
import com.android.chatapp.feature_chat.domain.model.Chat
import com.android.chatapp.feature_chat.domain.model.ChatType
import com.android.chatapp.feature_chat.presentation.components.ProfileImage
import com.android.chatapp.feature_chat.presentation.components.UserOnlineIndicator
import com.android.chatapp.ui.theme.ChatAppTheme

sealed class ChatItemEvent {
    data class ChatClicked(val chat: Chat) : ChatItemEvent()
    data class UserClicked(val chat: Chat) : ChatItemEvent()
}


@Composable
fun ChatItem(
    chat: Chat,
    modifier: Modifier = Modifier,
    onEvent: (ChatItemEvent) -> Unit
) {
    val profile = chat.user.profile
    Row(
        modifier = modifier
            .clickable { onEvent(ChatItemEvent.ChatClicked(chat)) }
            .padding(
                horizontal = MaterialTheme.spacing.medium,
                vertical = MaterialTheme.spacing.extraMedium
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            ProfileImage(
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.Center),
                profile = profile,
                size = 58.dp,
                shape = RoundedCornerShape(10.dp),
                onClick = { onEvent(ChatItemEvent.UserClicked(chat)) }
            )
            if (chat.user.session?.state == SessionState.ACTIVE)
                UserOnlineIndicator(Modifier.align(Alignment.TopEnd))
        }
        Column(
            modifier = Modifier
                .padding(start = 12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier
                        .weight(1f),
                    text = when (chat.type) {
                        ChatType.CONVERSATION -> profile.name
                        ChatType.ROOM -> chat.title ?: profile.name
                    },
                    style = MaterialTheme.typography.h6.copy(fontSize = 17.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = chat.latestMessage.created_at.readable,
                    style = MaterialTheme.typography.caption.copy(fontSize = 13.sp),
                    maxLines = 1,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                )
            }
            Row {
                Text(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .weight(1.0f),
                    text = chat.latestMessage.content,
                    style = MaterialTheme.typography.body2.copy(fontSize = 15.sp),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (false)
                    Text(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colors.primary,
                                RoundedCornerShape(5.dp)
                            )
                            .padding(bottom = 2.dp, start = 3.dp, end = 3.dp)
                            .align(Alignment.Bottom),
                        text = stringResource(id = R.string.chat_list_item_new_msg),
                        style = MaterialTheme.typography.caption,
                        color = Color.White
                    )
            }

        }
    }
}

@Preview(
    name = "light_mode",
    showBackground = true
)
@Preview(
    name = "dark_mode",
    uiMode = UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
fun ChatItemPreview() {
    ChatAppTheme {
        Surface {
            ChatItem(chat = CHAT_PREVIEW, onEvent = {})
        }
    }
}
package com.android.chatapp.feature_chat.presentation.search_profiles.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.chatapp.R
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.feature_authentication.domain.model.SessionState
import com.android.chatapp.feature_authentication.domain.model.USER_PREVIEW
import com.android.chatapp.feature_authentication.domain.model.User
import com.android.chatapp.feature_authentication.domain.model.name
import com.android.chatapp.feature_chat.presentation.components.ProfileImage
import com.android.chatapp.feature_chat.presentation.components.UserOnlineIndicator
import com.android.chatapp.ui.theme.ChatAppTheme

sealed class UserItemEvent {
    data class ChatClicked(val user: User) : UserItemEvent()
    data class UserClicked(val user: User) : UserItemEvent()
}


@Composable
fun UserItem(
    user: User,
    modifier: Modifier = Modifier,
    onEvent: (UserItemEvent) -> Unit
) {
    val profile = user.profile
    val context = LocalContext.current
    Row(
        modifier = modifier
            .clickable { onEvent(UserItemEvent.ChatClicked(user)) }
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
                onClick = { onEvent(UserItemEvent.UserClicked(user)) }
            )
            if (user.session?.state == SessionState.ACTIVE)
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
                    text = profile.name,
                    style = MaterialTheme.typography.h6.copy(fontSize = 17.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "@${user.username}",
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
                    text = profile.quote ?: profile.description
                    ?: stringResource(id = R.string.search_profiles_default_user_desc),
                    style = MaterialTheme.typography.body2.copy(fontSize = 15.sp),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
            UserItem(user = USER_PREVIEW, onEvent = {})
        }
    }
}
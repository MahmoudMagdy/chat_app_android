package com.android.chatapp.feature_chat.presentation.message_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.chatapp.R
import com.android.chatapp.core.presentation.util.EMPTY_TEXT
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.feature_authentication.domain.model.PROFILE_PREVIEW
import com.android.chatapp.feature_authentication.domain.model.Profile
import com.android.chatapp.feature_authentication.domain.model.name
import com.android.chatapp.feature_chat.presentation.components.ProfileImage

enum class ChatAppBarEvent {
    USER_CLICKED,
    BACK_BUTTON_CLICKED
}

@Composable
fun MessageListAppBar(profile: Profile?, onEvent: ((ChatAppBarEvent) -> Unit)) {
    TopAppBar(backgroundColor = MaterialTheme.colors.surface) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier.padding(start = MaterialTheme.spacing.extraSmall),
                onClick = { onEvent(ChatAppBarEvent.BACK_BUTTON_CLICKED) }
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = stringResource(id = R.string.gen_back_btn_desc)
                )
            }
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable(onClick = { onEvent(ChatAppBarEvent.USER_CLICKED) })
                    .padding(MaterialTheme.spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileImage(
                    profile = profile,
                    size = 32.dp,
                    shape = RoundedCornerShape(8.dp)
                )
                Text(
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.small),
                    text = profile?.name ?: EMPTY_TEXT,
                    style = MaterialTheme.typography.subtitle1
                )
            }
        }
    }
}

@Preview
@Composable
fun MessageListAppBarPreview() {
    MessageListAppBar(profile = PROFILE_PREVIEW, onEvent = {})
}
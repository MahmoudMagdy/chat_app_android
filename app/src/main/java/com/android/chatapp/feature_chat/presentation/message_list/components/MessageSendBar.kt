package com.android.chatapp.feature_chat.presentation.message_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.chatapp.R
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.ui.theme.Purple200

/**
 * @param message TODO: Actually need a state not just to disable the editor and the button while sending message
 */
@Composable
fun MessageSendBar(
    modifier: Modifier,
    message: String,
    onValueChange: (String) -> Unit,
    onSendClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colors.surface)
            .shadow(1.dp)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(1.0f),
            value = message,
            onValueChange = onValueChange,
            maxLines = 4,
            label = {
                Text(text = stringResource(id = R.string.msg_list_editor_hint))
            }
        )
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.extraMedium))
        IconButton(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(36.dp)
                .background(Purple200, CircleShape),
            onClick = onSendClicked
        ) {
            Icon(
                imageVector = Icons.Outlined.Send,
                contentDescription = stringResource(id = R.string.msg_list_send_record_btn_desc),
                tint = Color.White
            )
        }
    }
}

@Preview
@Composable
fun MessageSendBarPreview() {
    MessageSendBar(
        modifier = Modifier.padding(
            vertical = MaterialTheme.spacing.small,
            horizontal = 12.dp
        ),
        message = "",
        onValueChange = {},
        onSendClicked = {})
}
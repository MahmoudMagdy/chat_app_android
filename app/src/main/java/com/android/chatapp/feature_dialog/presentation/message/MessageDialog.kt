package com.android.chatapp.feature_dialog.presentation.message

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.android.chatapp.R
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.ui.theme.ChatAppTheme


const val NO_ACTION = -1

class MessageDialogState(
    @StringRes val title: Int,
    @StringRes val content: Int,
    @StringRes val actionTitle: Int = R.string.message_dialog_ok_btn,
    val action: Int = NO_ACTION
) {
    companion object {
        val Saver: Saver<MessageDialogState, *> = listSaver(
            save = {
                it.run { listOf(title, content, actionTitle, action) }
            },
            restore = {
                MessageDialogState(
                    title = it[0],
                    content = it[1],
                    actionTitle = it[2],
                    action = it[3]
                )
            }
        )
    }
}

@Composable
fun rememberMessageDialogState(
    @StringRes title: Int,
    @StringRes content: Int,
    @StringRes actionTitle: Int = R.string.message_dialog_ok_btn,
    action: Int = NO_ACTION
) =
    rememberSaveable(title, content, actionTitle, action, saver = MessageDialogState.Saver) {
        MessageDialogState(title, content, actionTitle, action)
    }


@Composable
fun MessageDialog(
    modifier: Modifier = Modifier,
    state: MessageDialogState,
    onEvent: (MessageDialogEvent) -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = { onEvent(MessageDialogEvent.DismissRequested) },
        title = {
            Text(text = stringResource(id = state.title))
        },
        text = {
            Text(text = stringResource(id = state.content))
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.small),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { onEvent(MessageDialogEvent.ActionClicked(state.action)) }) {
                    Text(text = stringResource(id = state.actionTitle))
                }
            }
        }
    )
}


@Preview(
    name = "MessageDialog(LightMode)",
    showBackground = true,
)
@Preview(
    name = "MessageDialog(DarkMode)",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun MessageDialogPreview() {
    ChatAppTheme {
        Surface {
            MessageDialog(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                state = rememberMessageDialogState(
                    title = R.string.gen_error_msg_title,
                    content = R.string.gen_error_msg_content
                ),
                onEvent = {}
            )
        }
    }
}
package com.android.chatapp.feature_dialog.presentation.progress

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.chatapp.R
import com.android.chatapp.core.presentation.util.EMPTY_TEXT
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.ui.theme.ChatAppTheme
import kotlinx.coroutines.delay


private const val DELAY_TIME: Long = 400

class ProgressDialogState(
    @StringRes val title: Int,
    @StringRes val caption: Int,
    @StringRes val actionTitle: Int = R.string.progress_dialog_cancel_title,
    val action: Int = 0
) {
    companion object {
        val Saver: Saver<ProgressDialogState, *> = listSaver(
            save = {
                it.run { listOf(title, caption, actionTitle, action) }
            },
            restore = {
                ProgressDialogState(
                    title = it[0],
                    caption = it[1],
                    actionTitle = it[2],
                    action = it[3]
                )
            }
        )
    }
}


@Composable
fun rememberProgressDialogState(
    @StringRes title: Int,
    @StringRes caption: Int,
    @StringRes actionTitle: Int = R.string.progress_dialog_cancel_title,
    action: Int = 0
) =
    rememberSaveable(title, caption, actionTitle, action, saver = ProgressDialogState.Saver) {
        ProgressDialogState(title, caption, actionTitle, action)
    }


@Composable
fun ProgressDialog(
    modifier: Modifier = Modifier,
    state: ProgressDialogState,
    onEvent: (ProgressDialogEvent) -> Unit
) {
    val baseCaption = stringResource(id = state.caption)
    var caption by rememberSaveable { mutableStateOf(baseCaption) }

    LaunchedEffect(key1 = state) {
        val dot = '.'
        var dots = dot.toString()
        while (true) {
            delay(DELAY_TIME)
            if (dots.length < 3) dots += dot
            else if (dots.length >= 3) dots = dot.toString()
            caption = "${baseCaption}$dots"
        }
    }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = { onEvent(ProgressDialogEvent.DISMISS_REQUESTED) },
        title = {
            Text(text = stringResource(id = state.title))
        },
        text = {
            Column {
                Text(text = EMPTY_TEXT, modifier = Modifier.height(MaterialTheme.spacing.small))
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Text(
                        text = caption,
                        modifier = Modifier.padding(MaterialTheme.spacing.extraMedium)
                    )
                }
            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.small),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { onEvent(ProgressDialogEvent.ACTION_CLICKED) }) {
                    Text(text = stringResource(id = state.actionTitle))
                }
            }
        }
    )
}


@Preview(
    name = "ProgressDialog(LightMode)",
    showBackground = true,
)
@Preview(
    name = "ProgressDialog(DarkMode)",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun ProgressDialogPreview() {
    ChatAppTheme {
        Surface {
            ProgressDialog(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                state = rememberProgressDialogState(
                    title = R.string.login_scr_email_dialog_title,
                    caption = R.string.login_scr_loading_data
                ),
                onEvent = {}
            )
        }
    }
}
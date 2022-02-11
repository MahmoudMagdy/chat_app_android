package com.android.chatapp.feature_authentication.presentation.reset_password

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.chatapp.R
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.feature_authentication.presentation.components.AppLogo
import com.android.chatapp.feature_authentication.presentation.components.EditableUserInput
import com.android.chatapp.feature_authentication.presentation.components.FilledLargeButton
import com.android.chatapp.feature_dialog.presentation.message.MessageDialog
import com.android.chatapp.ui.theme.ChatAppTheme
import kotlinx.coroutines.flow.collect
import java.util.*

private const val EFFECT_KEY = true

@Composable
fun ResetPasswordScreen(
    onPopBackStack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    val loading = viewModel.loading
    val msgDialog = viewModel.messageDialog.state
    LaunchedEffect(key1 = EFFECT_KEY) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                UiEvent.PopBackStack -> onPopBackStack()
            }
        }
    }
    Scaffold(topBar = {
        TopAppBar(backgroundColor = MaterialTheme.colors.surface) {
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(48.dp),
                    onClick = { viewModel.onEvent(ResetPasswordEvent.BackButtonClicked) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.gen_back_btn_desc)
                    )
                }
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(id = R.string.reset_password_rest_btn),
                    style = MaterialTheme.typography.h6.copy(fontSize = 18.sp),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }) {
        Box {
            AnimatedVisibility(visible = loading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .align(TopCenter)
                        .fillMaxWidth()
                )
            }
            Column(modifier = modifier.align(TopCenter)) {
                val centerAlignment = Modifier.align(CenterHorizontally)

                AppLogo(
                    modifier = centerAlignment
                        .padding(top = 28.dp, bottom = 40.dp)
                )
                Text(
                    modifier = centerAlignment,
                    text = stringResource(id = R.string.reset_password_forget_title),
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    modifier = centerAlignment.padding(top = MaterialTheme.spacing.small),
                    text = stringResource(id = R.string.reset_password_forget_desc),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body2.copy(fontSize = 15.sp)
                )
                EditableUserInput(
                    state = viewModel.emailState,
                    modifier = centerAlignment.padding(top = MaterialTheme.spacing.large),
                    onImeAction = { viewModel.onEvent(ResetPasswordEvent.EmailImeAction(this, it)) }
                )

                FilledLargeButton(
                    modifier = centerAlignment.padding(top = MaterialTheme.spacing.extraMedium),
                    text = stringResource(
                        id = R.string.reset_password_rest_btn
                    ).uppercase(Locale.getDefault()),
                    enabled = !loading
                ) {
                    viewModel.onEvent(ResetPasswordEvent.ResetPasswordClicked)
                }

            }
        }
        AnimatedVisibility(visible = msgDialog != null) {
            msgDialog?.apply {
                MessageDialog(state = this, onEvent = {
                    viewModel.onEvent(ResetPasswordEvent.OnMessageDialogEvent(it))
                })
            }
        }
    }
}


@Preview(
    name = "ResetPasswordScreen(LightMode)",
    showBackground = true,
    device = Devices.PIXEL_2
)
@Preview(
    name = "ResetPasswordScreen(DarkMode)",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_2
)
@Composable
fun LoginScreenPreview() {
    ChatAppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ResetPasswordScreen(
                onPopBackStack = {},
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
            )
        }
    }
}



package com.android.chatapp.feature_authentication.presentation.login

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.chatapp.R
import com.android.chatapp.feature_dialog.presentation.message.MessageDialog
import com.android.chatapp.feature_dialog.presentation.progress.ProgressDialog
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.feature_authentication.presentation.components.AppLogo
import com.android.chatapp.feature_authentication.presentation.components.EditableUserInput
import com.android.chatapp.feature_authentication.presentation.components.FilledLargeButton
import com.android.chatapp.feature_authentication.presentation.login.components.LoginTitle
import com.android.chatapp.feature_authentication.presentation.login.components.ObviousTextButton
import com.android.chatapp.feature_authentication.presentation.util.Screen
import com.android.chatapp.ui.theme.ChatAppTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


private const val EFFECT_KEY = true

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navigate: (Screen) -> Unit,
    launchChat: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val messageDialog = viewModel.messageDialog.state
    val progressDialog = viewModel.progressDialog.state
    val login = viewModel.login
    val scope = rememberCoroutineScope()
    val appContext = LocalContext.current.applicationContext
    LaunchedEffect(key1 = EFFECT_KEY) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> navigate(event.screen)
                UiEvent.LoginCompleted -> launchChat()
                is UiEvent.ShowSnackbar -> {
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = appContext.getString(event.message),
                            actionLabel = event.actionLabel?.let { appContext.getString(it) },
                            duration = event.duration
                        )
                        if (result == SnackbarResult.ActionPerformed && event.action != null)
                            viewModel.onEvent(event.action)
                    }
                }
            }
        }
    }

    Box(modifier = modifier) {
        Column {
            AppLogo(
                modifier = Modifier
                    .padding(
                        top = MaterialTheme.spacing.large,
                        bottom = MaterialTheme.spacing.medium
                    )
                    .align(Alignment.CenterHorizontally)
            )

            AnimatedVisibility(visible = login) {
                LoginTitle(title = R.string.login_scr_sign_in_desc_title)
            }

            AnimatedVisibility(visible = !login) {
                LoginTitle(title = R.string.login_scr_sign_up_desc_title)
            }

            val inputModifier = Modifier
                .padding(bottom = INPUT_VERTICAL_MARGIN)
                .align(Alignment.CenterHorizontally)

            EditableUserInput(
                state = viewModel.emailState,
                modifier = inputModifier.padding(top = 14.dp)
            )

            AnimatedVisibility(visible = !login) {
                EditableUserInput(
                    state = viewModel.usernameState,
                    modifier = inputModifier
                )
            }

            EditableUserInput(
                state = viewModel.passwordState,
                modifier = inputModifier,
                onImeAction = { viewModel.onEvent(LoginEvent.PasswordImeAction(this, it)) }
            )

            Box(modifier = Modifier.align(Alignment.End)) {
                androidx.compose.animation.AnimatedVisibility(visible = login) {
                    ObviousTextButton(
                        text = stringResource(id = R.string.login_scr_forgotten_pass_btn_title),
                        modifier = Modifier
                            .padding(top = BUTTON_VERTICAL_MARGIN)
                    ) {
                        viewModel.onEvent(LoginEvent.ForgetPasswordClickedClicked)
                    }
                }
                androidx.compose.animation.AnimatedVisibility(visible = !login) {
                    EditableUserInput(
                        state = viewModel.confirmPasswordState,
                        onImeAction = { viewModel.onEvent(LoginEvent.ConfirmPasswordImeAction(it)) }
                    )
                }
            }

            FilledLargeButton(
                text = stringResource(
                    id = if (login) R.string.login_scr_sign_in_title
                    else R.string.login_scr_sign_up_title
                ),
                modifier = Modifier.padding(top = BUTTON_VERTICAL_MARGIN)
            ) {
                viewModel.onEvent(LoginEvent.LoginButtonClicked)
            }

            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = BUTTON_VERTICAL_MARGIN),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(
                        id = if (login) R.string.login_scr_sign_up_desc
                        else R.string.login_scr_sign_in_desc
                    ),
                    style = MaterialTheme.typography.subtitle2
                )

                ObviousTextButton(
                    text = stringResource(
                        id = if (login) R.string.login_scr_sign_up_title
                        else R.string.login_scr_sign_in_title
                    ),
                    color = MaterialTheme.colors.primary
                ) {
                    viewModel.onEvent(LoginEvent.SwitchLoginUiClicked)
                }
            }
        }
    }

    AnimatedVisibility(visible = messageDialog != null) {
        messageDialog?.apply {
            MessageDialog(state = this, onEvent = {
                viewModel.onEvent(LoginEvent.OnMessageDialogEvent(it))
            })
        }
    }

    AnimatedVisibility(visible = progressDialog != null) {
        progressDialog?.apply {
            ProgressDialog(state = this, onEvent = {
                viewModel.onEvent(LoginEvent.OnProgressDialogEvent(it))
            })
        }
    }

    SnackbarHost(hostState = snackbarHostState)

}


private val INPUT_VERTICAL_MARGIN = 2.dp
private val BUTTON_VERTICAL_MARGIN = 12.dp

@Preview(
    name = "LoginScreen(LightMode)",
    showBackground = true,
    device = Devices.PIXEL_2
)
@Preview(
    name = "LoginScreen(DarkMode)",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_2
)
@Composable
fun LoginScreenPreview() {
    ChatAppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LoginScreen(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                navigate = {},
                launchChat = {},
            )
        }
    }
}
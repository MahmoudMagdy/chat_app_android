package com.android.chatapp.feature_authentication.presentation.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.android.chatapp.R
import com.android.chatapp.core.presentation.util.EMPTY_TEXT
import com.android.chatapp.core.presentation.util.imeAction
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.ui.theme.ChatAppTheme


class EditableUserInputState(
    @StringRes val hint: Int?,
    val isPassword: Boolean = false,
    initialAction: ImeAction = ImeAction.Next,
    initialText: String = EMPTY_TEXT,
    @StringRes initialError: Int? = null,
    initialEnabled: Boolean = true,
    initialPasswordVision: Boolean = false
) {
    var text by mutableStateOf(initialText)
        private set

    var error by mutableStateOf(initialError)
        private set

    var enabled by mutableStateOf(initialEnabled)
        private set

    var passwordVisibility by mutableStateOf(initialPasswordVision)
        private set

    val showError get() = error != null

    var imeAction by mutableStateOf(initialAction)
        private set


    fun setTextValue(value: String) {
        text = value
        if (error != null) error = null
    }

    fun setErrorValue(@StringRes id: Int?) {
        error = id
    }

    fun setEnabledValue(value: Boolean) {
        enabled = value
    }

    fun togglePasswordVisibility() {
        passwordVisibility = !passwordVisibility
    }

    fun setImeActionValue(value: ImeAction) {
        imeAction = value
    }

    companion object {
        val Saver: Saver<EditableUserInputState, *> = listSaver(
            save = {
                it.run {
                    listOf(
                        hint.toString(), isPassword.toString(), imeAction.toString(),
                        text, error.toString(), enabled.toString(), passwordVisibility.toString()
                    )
                }
            },
            restore = {
                EditableUserInputState(
                    hint = it[0].toInt(),
                    isPassword = it[1].toBooleanStrict(),
                    initialAction = it[2].imeAction,
                    initialText = it[2],
                    initialError = it[3].toInt(),
                    initialEnabled = it[4].toBooleanStrict(),
                    initialPasswordVision = it[5].toBooleanStrict()
                )
            }
        )
    }
}

@Composable
fun rememberEditableUserInputState(@StringRes hint: Int?, imeAction: ImeAction = ImeAction.Next) =
    rememberSaveable(hint, imeAction, saver = EditableUserInputState.Saver) {
        EditableUserInputState(hint = hint, initialAction = imeAction)
    }


@Composable
fun EditableUserInput(
    modifier: Modifier = Modifier,
    state: EditableUserInputState = rememberEditableUserInputState(hint = null),
    onImeAction: (KeyboardActionScope.(ImeAction) -> Unit)? = null,
    onValueChanged: (String) -> Unit = { state.setTextValue(it) },
    togglePasswordVisibility: () -> Unit = { state.togglePasswordVisibility() }
) {
    val imeAction = state.imeAction
    Column(modifier = modifier) {
        OutlinedTextField(
            value = state.text,
            enabled = state.enabled,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
            keyboardActions = KeyboardActions(onAny = {
                if (onImeAction != null) onImeAction(imeAction)
                else defaultKeyboardAction(imeAction)
            }),
            onValueChange = onValueChanged,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            singleLine = true,
            isError = state.showError,
            label = {
                Text(text = state.hint?.let { stringResource(id = it) } ?: EMPTY_TEXT)
            },
            visualTransformation =
            if (state.isPassword && !state.passwordVisibility)
                PasswordVisualTransformation()
            else VisualTransformation.None,
            trailingIcon = if (state.isPassword) {
                {
                    IconButton(onClick = togglePasswordVisibility) {
                        AnimatedVisibility(state.passwordVisibility) {
                            Icon(
                                imageVector = Icons.Filled.VisibilityOff,
                                contentDescription = stringResource(id = R.string.gen_password_toggle_invisible_desc)
                            )
                        }
                        AnimatedVisibility(!state.passwordVisibility) {
                            Icon(
                                imageVector = Icons.Filled.Visibility,
                                contentDescription = stringResource(id = R.string.gen_password_toggle_visible_desc)
                            )
                        }
                    }
                }
            } else null
        )
        val error = state.error
        AnimatedVisibility(error != null) {
            if (error != null)
                Text(
                    text = stringResource(id = error),
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(
                        top = MaterialTheme.spacing.extraSmall,
                        start = MaterialTheme.spacing.extraSmall
                    )
                )
        }
    }
}


@Preview(
    name = "EditableUserInput(LightMode)",
    showBackground = true,
    widthDp = 480,
)
@Preview(
    name = "EditableUserInput(DarkMode)",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES,
    widthDp = 480,
)
@Composable
fun EditableUserInputPreview() {
    ChatAppTheme {
        Surface {
            EditableUserInput(
                modifier = Modifier.padding(MaterialTheme.spacing.medium)
            )
        }
    }
}
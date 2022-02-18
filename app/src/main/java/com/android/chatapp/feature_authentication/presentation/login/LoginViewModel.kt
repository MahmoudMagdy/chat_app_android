package com.android.chatapp.feature_authentication.presentation.login

import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.chatapp.R
import com.android.chatapp.core.data.util.ApiException
import com.android.chatapp.core.data.util.NetworkResponseException
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_authentication.data.remote.dto.LoginRequest
import com.android.chatapp.feature_authentication.data.remote.dto.RegisterRequest
import com.android.chatapp.feature_authentication.data.remote.dto.UserResponse
import com.android.chatapp.feature_authentication.data.util.AuthApiExceptions
import com.android.chatapp.feature_authentication.domain.user_case.LoginUseCases
import com.android.chatapp.feature_authentication.domain.util.LoginFieldError
import com.android.chatapp.feature_authentication.presentation.components.EditableUserInputState
import com.android.chatapp.feature_authentication.presentation.util.Screen
import com.android.chatapp.feature_dialog.presentation.message.MessageDialogManager
import com.android.chatapp.feature_dialog.presentation.progress.ProgressDialogManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val cases: LoginUseCases) : ViewModel() {
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent get() = _uiEvent.receiveAsFlow()

    var login by mutableStateOf(true)
        private set


    val emailState = EditableUserInputState(
        hint = R.string.login_scr_acc_email_hint,
        initialAction = ImeAction.Next
    )
    val usernameState = EditableUserInputState(
        hint = R.string.login_scr_acc_username_hint,
        initialAction = ImeAction.Next
    )
    val passwordState = EditableUserInputState(
        hint = R.string.login_scr_acc_password_hint,
        isPassword = true,
        initialAction = ImeAction.Done
    )
    val confirmPasswordState = EditableUserInputState(
        hint = R.string.login_scr_con_acc_password_hint,
        isPassword = true,
        initialAction = ImeAction.Done
    )

    val messageDialog = MessageDialogManager()
    val progressDialog = ProgressDialogManager()

    private var loginJob: Job? = null

    init {
        clearLoginData()
    }

    fun onEvent(event: LoginEvent) {
        event.apply {
            when (this) {
                LoginEvent.LoginButtonClicked -> onLoginClicked()
                LoginEvent.ForgetPasswordClickedClicked -> sendUiEvent(UiEvent.Navigate(Screen.RESET_PASSWORD))
                LoginEvent.SwitchLoginUiClicked -> switchLoginUi()
                LoginEvent.DisplayRegisterUi -> if (login) switchLoginUi()
                is LoginEvent.ConfirmPasswordImeAction -> onConfirmPasswordImeAction(imeAction)
                is LoginEvent.PasswordImeAction -> onPasswordImeAction(actionScope, imeAction)
                is LoginEvent.OnMessageDialogEvent -> messageDialog.onEvent(this.event)
                is LoginEvent.OnProgressDialogEvent -> progressDialog.onEvent(this.event, loginJob)
            }
        }
    }

    private fun onPasswordImeAction(actionScope: KeyboardActionScope, imeAction: ImeAction) {
        if (imeAction == ImeAction.Done) onLoginClicked()
        else actionScope.defaultKeyboardAction(imeAction)
    }

    private fun onConfirmPasswordImeAction(imeAction: ImeAction) {
        if (imeAction == ImeAction.Done) onLoginClicked()
    }


    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private fun onLoginClicked() {
        if (cases.checkConnection()) {
            val email = emailState.text
            val password = passwordState.text
            if (login)
                cases.validateLoginData(email, password).onLoginValidation()
            else {
                val username = usernameState.text
                val conPassword = confirmPasswordState.text
                cases.validateRegisterData(email, username, password, conPassword)
                    .onRegisterValidation()
            }
        } else messageDialog.networkError
    }

    private fun Resource<LoginRequest, LoginFieldError>.onLoginValidation() {
        when (this) {
            is Resource.Success -> login(data)
            is Resource.Failure -> {
                errors.forEach { error ->
                    when (error) {
                        is LoginFieldError.Email -> emailState.setErrorValue(error.msg)
                        is LoginFieldError.Password -> passwordState.setErrorValue(error.msg)
                        else -> Unit
                    }
                }
            }
            else -> Unit
        }
    }

    private fun Resource<RegisterRequest, LoginFieldError>.onRegisterValidation() {
        when (this) {
            is Resource.Success -> register(data)
            is Resource.Failure -> {
                errors.forEach { error ->
                    when (error) {
                        is LoginFieldError.Email -> emailState.setErrorValue(error.msg)
                        is LoginFieldError.Username -> usernameState.setErrorValue(error.msg)
                        is LoginFieldError.Password -> passwordState.setErrorValue(error.msg)
                        is LoginFieldError.ConfirmPassword -> confirmPasswordState.setErrorValue(
                            error.msg
                        )
                        else -> Unit
                    }
                }
            }
            else -> Unit
        }
    }

    private fun login(loginRequest: LoginRequest) {
        loginJob = viewModelScope.launch(Dispatchers.IO) {
            resetErrors()
            cases.login(loginRequest).collect(::onLoginResult)
        }
    }

    private fun onLoginResult(result: Resource<UserResponse, ApiException>) {
        progressDialog.close
        result.apply {
            when (this) {
                is Resource.Loading -> progressDialog.show(
                    R.string.login_scr_email_dialog_title,
                    R.string.login_scr_loading_data
                )
                is Resource.Success -> onLoginSuccess(data)
                is Resource.Failure -> onLoginFailure(errors)
                is Resource.Error ->
                    if (throwable is NetworkResponseException)
                        messageDialog.networkError
                    else messageDialog.generalError
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun onLoginSuccess(data: UserResponse) {
        if (data.profile != null) {
            GlobalScope.launch { cases.enqueueNotificationWorker() }
            sendUiEvent(UiEvent.LoginCompleted)
        } else {
            sendUiEvent(UiEvent.Navigate(Screen.USER_INFO))
        }
    }

    private fun onLoginFailure(errors: List<ApiException>) {
        errors.forEach { error ->
            when (error) {
                AuthApiExceptions.credentialInvalid -> passwordState.setErrorValue(R.string.login_scr_incorrect_password_msg)
                AuthApiExceptions.userDisabled -> messageDialog.show(
                    R.string.gen_acc_disabled_error_title,
                    R.string.gen_acc_disabled_error_content
                )
                AuthApiExceptions.userNotFound -> showLoginSnack()
                AuthApiExceptions.emailInvalid -> emailState.setErrorValue(R.string.login_scr_invalid_email_msg)
                else -> messageDialog.generalError
            }
        }
    }


    private fun register(registerRequest: RegisterRequest) {
        resetErrors()
        loginJob = viewModelScope.launch(Dispatchers.IO) {
            cases.register(registerRequest).collect(::onRegisterResult)
        }
    }

    private fun onRegisterResult(result: Resource<UserResponse, ApiException>) {
        progressDialog.close
        result.apply {
            when (this) {
                is Resource.Loading -> progressDialog.show(
                    R.string.login_scr_email_dialog_title,
                    R.string.login_scr_loading_data
                )
                is Resource.Success -> onRegisterSuccess()
                is Resource.Failure -> onRegisterFailure(errors)
                is Resource.Error ->
                    if (throwable is NetworkResponseException)
                        messageDialog.networkError
                    else messageDialog.generalError

            }
        }
    }

    private fun onRegisterSuccess() {
        sendUiEvent(UiEvent.Navigate(Screen.USER_INFO))
    }

    private fun onRegisterFailure(errors: List<ApiException>) {
        errors.forEach { error ->
            when (error) {
                AuthApiExceptions.emailUnique -> emailState.setErrorValue(R.string.login_scr_unique_email_msg)
                AuthApiExceptions.emailInvalid -> emailState.setErrorValue(R.string.login_scr_invalid_email_msg)
                AuthApiExceptions.usernameUnique -> usernameState.setErrorValue(R.string.login_scr_unique_username_msg)
                AuthApiExceptions.usernameInvalid -> usernameState.setErrorValue(R.string.login_scr_invalid_username_msg)
                AuthApiExceptions.passwordWeak -> passwordState.setErrorValue(R.string.login_scr_weak_password_msg)
                AuthApiExceptions.passwordStrong -> passwordState.setErrorValue(R.string.login_scr_incorrect_password_msg)
                else -> messageDialog.generalError
            }
        }
    }


    private fun switchLoginUi() {
        login = !login
        passwordState.setImeActionValue(if (login) ImeAction.Done else ImeAction.Next)
        resetErrors()
    }

    private fun resetErrors() {
        emailState.setErrorValue(null)
        usernameState.setErrorValue(null)
        passwordState.setErrorValue(null)
        confirmPasswordState.setErrorValue(null)
    }


    private fun showLoginSnack() {
        sendUiEvent(
            UiEvent.ShowSnackbar(
                message = R.string.login_scr_no_user_content,
                duration = SnackbarDuration.Long,
                actionLabel = R.string.login_scr_sign_up_title,
                action = LoginEvent.DisplayRegisterUi
            )
        )
    }


    private fun clearLoginData() {
        cases.logout()
    }

}


package com.android.chatapp.feature_authentication.presentation.reset_password

import androidx.compose.foundation.text.KeyboardActionScope
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
import com.android.chatapp.feature_dialog.presentation.message.MessageDialogManager
import com.android.chatapp.feature_authentication.data.remote.dto.ResetPasswordRequest
import com.android.chatapp.feature_authentication.data.remote.dto.ResetPasswordResponse
import com.android.chatapp.feature_authentication.data.util.AuthApiExceptions
import com.android.chatapp.feature_authentication.domain.user_case.ResetPasswordExceededLimits
import com.android.chatapp.feature_authentication.domain.user_case.ResetPasswordUseCases
import com.android.chatapp.feature_authentication.domain.util.LoginFieldError
import com.android.chatapp.feature_authentication.presentation.components.EditableUserInputState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ResetPasswordViewModel @Inject constructor(private val cases: ResetPasswordUseCases) :
    ViewModel() {
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent get() = _uiEvent.receiveAsFlow()

    val emailState = EditableUserInputState(
        hint = R.string.login_scr_acc_email_hint,
        initialAction = ImeAction.Done
    )

    var loading by mutableStateOf(false)
        private set

    private fun displayLoadingUi(display: Boolean = true) {
        loading = display
        emailState.setEnabledValue(!display)
    }


    val messageDialog = MessageDialogManager()

    private var resetJob: Job? = null

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    fun onEvent(event: ResetPasswordEvent) {
        event.apply {
            when (this) {
                is ResetPasswordEvent.EmailImeAction -> onEmailImeAction(actionScope, imeAction)
                is ResetPasswordEvent.OnMessageDialogEvent -> messageDialog.onEvent(this.event)
                ResetPasswordEvent.ResetPasswordClicked -> onResetPasswordClicked()
                ResetPasswordEvent.BackButtonClicked -> onBackBtnClicked()
            }
        }
    }

    private fun onEmailImeAction(actionScope: KeyboardActionScope, imeAction: ImeAction) {
        if (imeAction == ImeAction.Done)
            onResetPasswordClicked()
        else actionScope.defaultKeyboardAction(imeAction)
    }

    private fun onResetPasswordClicked() {
        if (cases.checkConnection()) {
            cases.validateResetPasswordData(emailState.text).apply {
                when (this) {
                    is Resource.Success -> checkResetLimitations(data)
                    is Resource.Failure -> {
                        errors.forEach { error ->
                            when (error) {
                                is LoginFieldError.Email -> emailState.setErrorValue(error.msg)
                                else -> Unit
                            }
                        }
                    }
                    else -> Unit
                }
            }
        } else messageDialog.networkError
    }

    private fun checkResetLimitations(email: String) {
        try {
            resetPassword(cases.checkResetPasswordLimit(email))
        } catch (ex: ResetPasswordExceededLimits) {
            messageDialog.show(
                R.string.reset_password_time_limit_msg_title,
                R.string.reset_password_time_limit_msg_content
            )
        }
    }

    private fun resetPassword(resetRequest: ResetPasswordRequest) {
        resetJob = viewModelScope.launch(Dispatchers.IO) {
            cases.resetPassword(resetRequest).collect(::onResetPasswordResult)
        }
    }

    private fun onResetPasswordResult(result: Resource<ResetPasswordResponse, ApiException>) {
        result.apply {
            displayLoadingUi(false)
            when (this) {
                is Resource.Loading -> displayLoadingUi()
                is Resource.Success -> {
                    messageDialog.show(
                        R.string.reset_password_success_msg_title,
                        R.string.reset_password_success_msg_content
                    )
                }
                is Resource.Failure -> onFailure(errors)
                is Resource.Error -> if (throwable is NetworkResponseException)
                    messageDialog.networkError
                else messageDialog.generalError
            }
        }
    }

    private fun onFailure(errors: List<ApiException>) {
        errors.forEach { error ->
            when (error) {
                AuthApiExceptions.userDisabled -> messageDialog.show(
                    R.string.gen_acc_disabled_error_title,
                    R.string.reset_password_disabled_error_content
                )
                AuthApiExceptions.userNotFound -> emailState.setErrorValue(R.string.login_scr_no_user_content)
                AuthApiExceptions.emailInvalid -> emailState.setErrorValue(R.string.login_scr_invalid_email_msg)
                else -> messageDialog.generalError
            }
        }
    }


    private fun onBackBtnClicked() {
        resetJob?.cancel()
        sendUiEvent(UiEvent.PopBackStack)
    }
}
package com.android.chatapp.feature_authentication.domain.user_case

import com.android.chatapp.core.domain.user_case.CheckConnection
import javax.inject.Inject

data class ResetPasswordUseCases @Inject constructor(
    val resetPassword: ResetPassword,
    val validateResetPasswordData: ValidateResetPasswordData,
    val checkResetPasswordLimit: CheckResetPasswordLimit,
    val checkConnection: CheckConnection,
)
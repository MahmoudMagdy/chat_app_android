package com.android.chatapp.feature_authentication.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordResponse(@SerialName("success")
                                 val successMsg: String)

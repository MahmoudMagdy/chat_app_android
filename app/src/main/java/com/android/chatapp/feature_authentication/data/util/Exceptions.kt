package com.android.chatapp.feature_authentication.data.util

import java.net.ConnectException

class EmailResetDateNotFoundException : ConnectException()

sealed class UnauthorizedAccessException : SecurityException() {
    object NoLoginUser : UnauthorizedAccessException()
    object AnonymousLogin : UnauthorizedAccessException()
    object NoUserID : UnauthorizedAccessException()
}

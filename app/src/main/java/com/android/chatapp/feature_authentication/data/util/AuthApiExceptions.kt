package com.android.chatapp.feature_authentication.data.util

import com.android.chatapp.core.data.util.ApiException

object AuthApiExceptions {
    val userDisabled = ApiException("user_disabled", "User disabled", statusCode = 403)
    val userNotFound = ApiException("user_not_found", "User not found", listOf("email"), 404)
    val usernameInvalid = ApiException("username_invalid", "The username should only contains alphanumeric characters.", listOf("username"))
    val usernameUnique = ApiException("username_unique", "user with this username already exists.", listOf("username"))
    val emailInvalid = ApiException("email_invalid", "Enter a valid email address.", listOf("email"))
    val emailUnique = ApiException("email_unique", "user with this email already exists.", listOf("email"))
    val passwordWeak = ApiException("password_min_length", "Ensure this field has at least 6 characters.", listOf("password"))
    val passwordStrong = ApiException("password_max_length", "Ensure this field has no more than 68 characters.", listOf("password"))
    val credentialInvalid = ApiException("credentials_invalid", "Invalid Credentials, Try again.", statusCode = 401)
    val tokenInvalid = ApiException("token_not_valid", "Token is invalid or expired.", statusCode = 401)
    val dataInvalid = ApiException("invalid_data", "Data not valid.")
    val profileExists = ApiException("profile_already_exists", "User Profile already exists.", listOf("profile"), 406)
    val profileNotFound = ApiException("profile_not_found", "Profile not found", listOf("profile"), 404)
    val notAuthenticated = ApiException("not_authenticated", "Authentication credentials were not provided.", statusCode = 401)
    val notAuthenticatedToken = ApiException("token_invalid_expired", "Token is invalid or expired.", statusCode = 401)
}
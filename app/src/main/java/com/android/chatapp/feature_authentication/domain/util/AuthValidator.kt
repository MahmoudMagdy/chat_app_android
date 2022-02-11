package com.android.chatapp.feature_authentication.domain.util

private const val MINIMUM_PASSWORD_LENGTH = 6

internal val String.validEmail
    get() = run {
        val split = split('@')
        split.size == 2 && Regex("^[a-zA-Z0-9._-]+\$").matches(split[0]) &&
                split[1].split('.').run {
                    size == 2 && Regex("^[a-zA-Z0-9]+\$").let {
                        it.matches(this[0]) && it.matches(
                            this[1]
                        )
                    }
                }
    }

internal val String.validPassword get() = length >= MINIMUM_PASSWORD_LENGTH

internal val String.validUsername get() = Regex("^[A-Za-z0-9_]*\$").matches(this)
package com.android.chatapp.feature_authentication.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Session(
    val state: SessionState,
    val startedAt: Instant,
    val endedAt: Instant?
)

val SESSION_PREVIEW = Session(SessionState.ACTIVE, Clock.System.now(), null)
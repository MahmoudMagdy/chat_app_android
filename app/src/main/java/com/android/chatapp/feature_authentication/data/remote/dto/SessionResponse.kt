package com.android.chatapp.feature_authentication.data.remote.dto

import com.android.chatapp.feature_authentication.domain.model.Session
import com.android.chatapp.feature_authentication.domain.model.SessionState
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SessionResponse(
    val state: SessionState,
    @SerialName("started_at")
    val startedAt: Instant,
    @SerialName("ended_at")
    val endedAt: Instant?
)

val SessionResponse.model get() = Session(state, startedAt, endedAt)
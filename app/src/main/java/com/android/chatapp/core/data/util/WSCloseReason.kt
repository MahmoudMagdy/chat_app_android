package com.android.chatapp.core.data.util

import io.ktor.http.cio.websocket.*

enum class WSCloseReason(val code: Short) {
    UNKNOWN(-1),
    USER_NOT_FOUND(4003),
    UNAUTHORIZED_CHAT_ACCESS(4004),
    CHAT_NOT_FOUND(3003),
    SENT_ITEM_INVALID(3001),
    CLOSED_ABNORMALLY(1006),
    NOT_CONSISTENT(1007),
    VIOLATED_POLICY(1008),
    TOO_BIG(1009),
    NO_EXTENSION(1010),
    INTERNAL_ERROR(1011),
    SERVICE_RESTART(1012),
    TRY_AGAIN_LATER(1013);

    companion object {
        private val byCodeMap = values().associateBy { it.code }
        infix fun by(code: Short?): WSCloseReason = byCodeMap[code ?: -1] ?: UNKNOWN
        infix fun by(reason: CloseReason?): WSCloseReason = byCodeMap[reason?.code ?: -1] ?: UNKNOWN
    }
}
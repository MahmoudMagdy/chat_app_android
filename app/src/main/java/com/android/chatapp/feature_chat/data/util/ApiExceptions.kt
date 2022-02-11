package com.android.chatapp.feature_chat.data.util

import com.android.chatapp.core.data.util.ApiException

object ApiExceptions {
    val invalidPage =
        ApiException("invalid_page", "Invalid page.", statusCode = 404)
    val noChatWithId =
        ApiException("no_chat_with_id", "No chat with provided id.", statusCode = 400)
    val notChatMember =
        ApiException("not_chat_member", "User is not chat member.", statusCode = 403)
    val noChatRequested =
        ApiException("no_chat_requested", "No chat requested.", statusCode = 400)
    val noChatMatched =
        ApiException("no_chat_matched", "No chat matched.", statusCode = 400)
    val noChatUserWithId =
        ApiException("no_chat_user_with_id", "No chat with provided id.", statusCode = 400)
    val chattingSelfNotPermitted =
        ApiException("chatting_self_not_permitted", "Chatting self not permitted.", statusCode = 400)
}
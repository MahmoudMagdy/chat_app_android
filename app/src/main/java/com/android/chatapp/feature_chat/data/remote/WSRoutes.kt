package com.android.chatapp.feature_chat.data.remote

import com.android.chatapp.core.data.remote.WSRoutes

object WSRoutes {
    private const val CHAT_URL = "${WSRoutes.BASE_URL}/chat"
    const val CHAT_LIST_URL = "$CHAT_URL/list/"
    fun getChatUrl(id: Long) = "${CHAT_URL}/$id/"
}
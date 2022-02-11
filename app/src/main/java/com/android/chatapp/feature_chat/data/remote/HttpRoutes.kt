package com.android.chatapp.feature_chat.data.remote

import com.android.chatapp.core.data.remote.HttpRoutes.BASE_URL

object HttpRoutes {
    private const val CHAT_URL = "$BASE_URL/chat"
    private const val CHAT_CREATE_URL = "$CHAT_URL/create/"
    const val CHAT_LIST_URL = "$CHAT_URL/list/"
    private const val CHAT_ITEM_URL = "$CHAT_URL/detail/"
    fun getChatsUrl(id: Long, page: Int, limit: Int) =
        "$CHAT_URL/$id/?page=$page&page_size=$limit"

    fun getChatWithIdUrl(id: Long) =
        "$CHAT_ITEM_URL?_id=$id"

    fun getChatWithUserIdUrl(id: Long) =
        "$CHAT_ITEM_URL?uid=$id"

    fun createChatUrl(oid: Long): String = "$CHAT_CREATE_URL$oid/"

}
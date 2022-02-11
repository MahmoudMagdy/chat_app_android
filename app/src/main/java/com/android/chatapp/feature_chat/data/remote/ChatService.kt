package com.android.chatapp.feature_chat.data.remote

import com.android.chatapp.core.data.util.BASE_PAGE_SIZE
import com.android.chatapp.core.data.util.PaginationResult
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_chat.data.remote.dto.ChatResponse
import com.android.chatapp.feature_chat.data.remote.dto.MessageRequest
import com.android.chatapp.feature_chat.data.remote.dto.MessageResponse

interface ChatService {
    suspend fun createChat(
        oid: Long, request: MessageRequest
    ): Resource.Success<ChatResponse>

    suspend fun getChats(): Resource.Success<List<ChatResponse>>
    suspend fun getChatWithId(id: Long): Resource.Success<ChatResponse>
    suspend fun getChatWithUserId(id: Long): Resource.Success<ChatResponse>
    suspend fun getChatMessages(
        id: Long,
        page: Int,
        limit: Int = BASE_PAGE_SIZE
    ): Resource.Success<PaginationResult<MessageResponse>>
}
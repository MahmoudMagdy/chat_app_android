package com.android.chatapp.feature_chat.data.remote

import com.android.chatapp.core.data.util.PaginationResult
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_chat.data.remote.dto.ChatResponse
import com.android.chatapp.feature_chat.data.remote.dto.MessageRequest
import com.android.chatapp.feature_chat.data.remote.dto.MessageResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class ChatServiceImpl(private val client: HttpClient) : ChatService {

    override suspend fun createChat(
        oid: Long,
        request: MessageRequest
    ): Resource.Success<ChatResponse> =
        client.post {
            url(HttpRoutes.createChatUrl(oid))
            contentType(ContentType.Application.Json)
            body = request
        }


    override suspend fun getChats(): Resource.Success<List<ChatResponse>> =
        client.get { url(HttpRoutes.CHAT_LIST_URL) }

    override suspend fun getChatWithId(id: Long): Resource.Success<ChatResponse> =
        client.get { url(HttpRoutes.getChatWithIdUrl(id)) }

    override suspend fun getChatWithUserId(id: Long): Resource.Success<ChatResponse> =
        client.get { url(HttpRoutes.getChatWithUserIdUrl(id)) }


    override suspend fun getChatMessages(
        id: Long,
        page: Int,
        limit: Int
    ): Resource.Success<PaginationResult<MessageResponse>> =
        client.get { url(HttpRoutes.getChatsUrl(id, page, limit)) }
}
package com.android.chatapp.feature_chat.domain.repository

import com.android.chatapp.core.data.util.ApiException
import com.android.chatapp.core.data.util.PaginationResult
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.core.data.util.WSCloseReason
import com.android.chatapp.feature_chat.data.remote.dto.MessageRequest
import com.android.chatapp.feature_chat.domain.model.Chat
import com.android.chatapp.feature_chat.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChats(): Flow<Resource<List<Chat>, ApiException>>
    fun getChat(cid: Long?, oid: Long?): Flow<Resource<Chat, ApiException>>
    fun createChat(
        oid: Long,
        request: MessageRequest
    ): Flow<Resource<Chat, ApiException>>

    fun getChatMessages(
        id: Long,
        page: Int,
        latestMessageId: Long,
    ): Flow<Resource<PaginationResult<Message>, ApiException>>

    suspend fun connectToChats(): Resource<Unit, Nothing>
    suspend fun disconnectFromChats(block: (WSCloseReason) -> Unit)
    fun receiveChats(): Flow<Chat>
    suspend fun closeChats()


    suspend fun connectToChatMessages(id: Long): Resource<Unit, Nothing>

    /**
     * @param id will used in future when i change the plan on [ChatSocketService]
     **/
    fun isConnectToChatMessages(id: Long): Boolean
    suspend fun disconnectFromChatMessages(block: (WSCloseReason) -> Unit)
    fun receiveChatMessages(): Flow<Message>
    suspend fun sendChatMessages(request: MessageRequest): Resource<Unit, Nothing>
    suspend fun closeChatMessages()
}
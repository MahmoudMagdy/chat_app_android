package com.android.chatapp.feature_chat.data.repository

import com.android.chatapp.core.data.util.*
import com.android.chatapp.feature_authentication.data.local.entity.ProfileEntity
import com.android.chatapp.feature_authentication.data.local.entity.ProfileMediaEntity
import com.android.chatapp.feature_authentication.data.local.entity.UserEntity
import com.android.chatapp.feature_authentication.data.provider.token.TokenProvider
import com.android.chatapp.feature_authentication.data.remote.dto.UserResponse
import com.android.chatapp.feature_authentication.data.remote.dto.entity
import com.android.chatapp.feature_authentication.data.util.UnauthorizedAccessException
import com.android.chatapp.feature_chat.data.local.ChatDao
import com.android.chatapp.feature_chat.data.local.MessageDao
import com.android.chatapp.feature_chat.data.local.entity.*
import com.android.chatapp.feature_chat.data.remote.ChatService
import com.android.chatapp.feature_chat.data.remote.ChatSocketService
import com.android.chatapp.feature_chat.data.remote.ChatsSocketService
import com.android.chatapp.feature_chat.data.remote.dto.*
import com.android.chatapp.feature_chat.domain.model.Chat
import com.android.chatapp.feature_chat.domain.model.Message
import com.android.chatapp.feature_chat.domain.repository.ChatRepository
import com.android.chatapp.feature_chat.presentation.NO_ID
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ChatRepositoryImpl(
    private val service: ChatService,
    private val chatsSocketService: ChatsSocketService,
    private val chatSocketService: ChatSocketService,
    private val dao: ChatDao,
    private val messageDao: MessageDao,
    private val tokenProvider: TokenProvider,
) : ChatRepository {

    //TODO: handle unauthorized access every where in chat
    @Throws(UnauthorizedAccessException::class)
    override fun getChats(): Flow<Resource<List<Chat>, ApiException>> = flow {
        emit(Resource.Loading())
        val uid = tokenProvider.uid() ?: throw UnauthorizedAccessException.NoUserID
        coroutineScope {
            val chats =
                async { dao.getChatsWithAllData(uid).takeIf { it.isNotEmpty() }?.models(null) }
            val chatsResponse =
                async { safeApiCall<List<ChatResponse>, ApiException>(service::getChats) }
            val dbResult = chats.await()
            if (dbResult != null) emit(Resource.Loading(dbResult))
            when (val remoteResult = chatsResponse.await()) {
                is Resource.Success -> {
                    if (remoteResult.data.isNotEmpty()) {
                        val users = persistFetchedChatData(uid, remoteResult.data)
                        val newChats = dao.getChatsWithAllData(uid).models(users)
                        emit(Resource.Success(newChats))
                    } else emit(Resource.Success(dbResult ?: listOf()))
                }
                is Resource.Error -> emit(Resource.Error(remoteResult.throwable, dbResult))
                is Resource.Failure -> emit(Resource.Failure(remoteResult.errors, dbResult))
                is Resource.Loading -> Unit
            }
        }
    }

    override fun getChat(cid: Long?, oid: Long?): Flow<Resource<Chat, ApiException>> = flow {
        emit(Resource.Loading())
        val chatResponse: Resource<ChatResponse, ApiException> =
            if (cid != null && cid != NO_ID) safeApiCall { service.getChatWithId(cid) }
            else safeApiCall { service.getChatWithUserId(oid!!) }
        onChatResponse(chatResponse)
    }

    override fun createChat(
        oid: Long,
        request: MessageRequest
    ): Flow<Resource<Chat, ApiException>> = flow {
        emit(Resource.Loading())
        val chatResponse: Resource<ChatResponse, ApiException> =
            safeApiCall { service.createChat(oid, request) }
        onChatResponse(chatResponse)
    }

    @Throws(UnauthorizedAccessException::class)
    private suspend fun FlowCollector<Resource<Chat, ApiException>>.onChatResponse(chatResponse: Resource<ChatResponse, ApiException>) {
        val uid = tokenProvider.uid() ?: throw UnauthorizedAccessException.NoUserID
        when (chatResponse) {
            is Resource.Success -> {
                val chat = chatResponse.data
                val (chatEntity, messageEntity, userEntities, userChatCrossRefEntities,
                    profileEntities, mediaEntities) = ChatEntityHolder(uid, chat)
                dao.insertChatWithAllData(
                    chatEntity,
                    messageEntity,
                    userEntities,
                    userChatCrossRefEntities,
                    profileEntities,
                    mediaEntities
                )
                emit(Resource.Success(dao.getChatWithAllData(uid, chat.id).model(chat.users)))
            }
            is Resource.Error -> emit(Resource.Error(chatResponse.throwable))
            is Resource.Failure -> emit(Resource.Failure(chatResponse.errors))
            is Resource.Loading -> Unit
        }
    }

    private suspend fun persistFetchedChatData(
        uid: Long,
        data: List<ChatResponse>
    ): List<UserResponse> {
        val chatEntities = mutableListOf<ChatEntity>()
        val messageEntities = mutableListOf<MessageEntity>()
        val userEntities = mutableListOf<UserEntity>()
        val userChatCrossRefEntities = mutableListOf<UserChatCrossRef>()
        val profileEntities = mutableListOf<ProfileEntity>()
        val mediaEntities = mutableListOf<ProfileMediaEntity>()
        val users = mutableListOf<UserResponse>()
        data.forEach { chat ->
            val (chatEntity, messageEntity, _userEntities, _userChatCrossRefEntities,
                _profileEntities, _mediaEntities) = ChatEntityHolder(uid, chat)
            chatEntities.add(chatEntity)
            messageEntities.add(messageEntity)
            userEntities.addAll(_userEntities)
            userChatCrossRefEntities.addAll(_userChatCrossRefEntities)
            profileEntities.addAll(_profileEntities)
            mediaEntities.addAll(_mediaEntities)
            users.addAll(chat.users)
        }
        dao.insertChatsWithAllData(
            chatEntities,
            messageEntities,
            userEntities,
            userChatCrossRefEntities,
            profileEntities,
            mediaEntities
        )
        return users
    }

    override suspend fun connectToChats(): Resource<Unit, Nothing> =
        safeApiConnection(chatsSocketService::connect)

    override suspend fun disconnectFromChats(block: (WSCloseReason) -> Unit) =
        chatsSocketService.disconnect { reason -> block(WSCloseReason by reason) }


    @Throws(UnauthorizedAccessException::class)
    override suspend fun receiveChats(): Flow<Chat> {
        val uid = tokenProvider.uid() ?: throw UnauthorizedAccessException.NoUserID
        return chatsSocketService.receive().map { chat ->
            val (chatEntity, messageEntity, userEntities, userChatCrossRefEntities,
                profileEntities, mediaEntities) = ChatEntityHolder(uid, chat)
            dao.insertChatWithAllData(
                chatEntity,
                messageEntity,
                userEntities,
                userChatCrossRefEntities,
                profileEntities,
                mediaEntities
            )
            dao.getChatWithAllData(uid, chat.id).model(chat.users)
        }
    }

    override suspend fun closeChats() = chatsSocketService.close()

    data class ChatEntityHolder(
        val chatEntity: ChatEntity,
        val messageEntity: MessageEntity,
        val userEntities: List<UserEntity>,
        val userChatCrossRefEntities: List<UserChatCrossRef>,
        val profileEntities: List<ProfileEntity>,
        val mediaEntities: List<ProfileMediaEntity>
    ) {
        companion object {
            operator fun invoke(userId: Long, chat: ChatResponse): ChatEntityHolder {
                val userEntities = mutableListOf<UserEntity>()
                val userChatCrossRefEntities = mutableListOf<UserChatCrossRef>()
                val profileEntities = mutableListOf<ProfileEntity>()
                val mediaEntities = mutableListOf<ProfileMediaEntity>()
                chat.users.forEach { user ->
                    userEntities.add(user.entity)
                    userChatCrossRefEntities.add(UserChatCrossRef(user.id, chat.id))
                    if (user.profile != null) {
                        profileEntities.add(user.profile.entity)
                        if (user.profile.latestImage != null)
                            mediaEntities.add(user.profile.latestImage.entity)
                    }
                }
                userChatCrossRefEntities.add(UserChatCrossRef(userId, chat.id))
                return ChatEntityHolder(
                    chat.entity,
                    chat.latestMessage.entity,
                    userEntities,
                    userChatCrossRefEntities,
                    profileEntities,
                    mediaEntities
                )
            }
        }
    }

    //Message List Part
    override fun getChatMessages(
        id: Long,
        page: Int,
        latestMessageId: Long
    ): Flow<Resource<PaginationResult<Message>, ApiException>> = flow {
        emit(Resource.Loading())
        coroutineScope {
            val messages = async {
                if (latestMessageId == EMPTY_LATEST_ITEM_ID) messageDao.getStartChatMessages(id)
                else messageDao.getChatMessages(id, latestMessageId)
            }
            val messagesResponse = async {
                safeApiCall<PaginationResult<MessageResponse>, ApiException> {
                    service.getChatMessages(id, page)
                }
            }
            val dbResult = messages.await().takeIf { it.isNotEmpty() }?.models
            val dbPaginationResult = dbResult?.let { PaginationResult(null, null, it) }
            if (dbPaginationResult != null) emit(Resource.Loading(dbPaginationResult))
            when (val remoteResult = messagesResponse.await()) {
                is Resource.Success -> {
                    emit(
                        if (remoteResult.data.results.isNotEmpty()) {
                            persistFetchedMessages(remoteResult.data.results)
                            val newMessages = messageDao.run {
                                if (latestMessageId == EMPTY_LATEST_ITEM_ID) getStartChatMessages(id)
                                else getChatMessages(id, latestMessageId)
                            }
                            remoteResult.data.run {
                                Resource.Success(
                                    PaginationResult(next, previous, newMessages.models)
                                )
                            }
                        } else remoteResult.data.run {
                            Resource.Success(
                                PaginationResult(next, previous, dbResult ?: listOf())
                            )
                        }
                    )
                }
                is Resource.Error ->
                    emit(Resource.Error(remoteResult.throwable, dbPaginationResult))
                is Resource.Failure ->
                    emit(Resource.Failure(remoteResult.errors, dbPaginationResult))
                is Resource.Loading -> Unit
            }
        }
    }

    private fun persistFetchedMessages(messages: List<MessageResponse>) {
        messageDao.insertList(messages.map { it.entity })
    }

    override suspend fun connectToChatMessages(id: Long): Resource<Unit, Nothing> =
        safeApiConnection { chatSocketService.connect(id) }

    override fun isConnectToChatMessages(id: Long): Boolean = chatSocketService.connected

    override suspend fun disconnectFromChatMessages(block: (WSCloseReason) -> Unit) =
        chatSocketService.disconnect { reason -> block(WSCloseReason by reason) }


    override fun receiveChatMessages(): Flow<Message> =
        chatSocketService.receive().map { message ->
            messageDao.insert(message.entity)
            message.model
        }


    override suspend fun closeChatMessages() = chatSocketService.close()

    override suspend fun sendChatMessages(request: MessageRequest): Resource<Unit, Nothing> =
        safeApiMessaging { chatSocketService.send(request) }


}
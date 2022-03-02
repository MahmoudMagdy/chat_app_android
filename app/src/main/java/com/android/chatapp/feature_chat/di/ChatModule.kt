package com.android.chatapp.feature_chat.di

import com.android.chatapp.core.data.local.ChatAppDatabase
import com.android.chatapp.core.data.remote.RemoteDataSource
import com.android.chatapp.feature_authentication.data.provider.token.TokenProvider
import com.android.chatapp.feature_chat.data.repository.ChatRepositoryImpl
import com.android.chatapp.feature_chat.domain.repository.ChatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {
    @Provides
    @Singleton
    fun provideChatRepository(
        remoteDataSource: RemoteDataSource,
        db: ChatAppDatabase,
        tokenProvider: TokenProvider
    ): ChatRepository =
        ChatRepositoryImpl(
            service = remoteDataSource.chatService,
            chatsSocketService = remoteDataSource.chatsSocketService,
            chatSocketService = remoteDataSource.chatSocketService,
            dao = db.chatDao(),
            messageDao = db.messageDao(),
            tokenProvider = tokenProvider
        )
}
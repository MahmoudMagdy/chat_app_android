package com.android.chatapp.feature_notification.di

import com.android.chatapp.core.data.local.ChatAppDatabase
import com.android.chatapp.core.data.remote.RemoteDataSource
import com.android.chatapp.feature_notification.data.repository.NotificationRepositoryImpl
import com.android.chatapp.feature_notification.domain.repository.NotificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {
    @Provides
    @Singleton
    fun provideNotificationRepository(
        remoteDataSource: RemoteDataSource,
        db: ChatAppDatabase,
    ): NotificationRepository = NotificationRepositoryImpl(
        remoteDataSource.notificationsSocketService,
        db.messageDao()
    )
}
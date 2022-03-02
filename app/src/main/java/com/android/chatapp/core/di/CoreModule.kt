package com.android.chatapp.core.di

import android.content.Context
import androidx.work.WorkManager
import com.android.chatapp.core.data.local.ChatAppDatabase
import com.android.chatapp.core.data.remote.RemoteDataSource
import com.android.chatapp.core.data.remote.RemoteDataSourceImpl
import com.android.chatapp.core.data.repository.StorageRepositoryImpl
import com.android.chatapp.core.domain.repository.StorageRepository
import com.android.chatapp.feature_authentication.data.provider.token.TokenProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    fun provideWorkerManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)

    @Provides
    @Singleton
    fun provideLocalDatabase(@ApplicationContext context: Context): ChatAppDatabase =
        ChatAppDatabase(context)


    @Provides
    @Singleton
    fun provideRemoteDataSource(tokenProvider: TokenProvider): RemoteDataSource {
        return RemoteDataSourceImpl(tokenProvider)
    }

    @Provides
    @Singleton
    fun provideStorageRepository(remoteDataSource: RemoteDataSource): StorageRepository =
        StorageRepositoryImpl(remoteDataSource.storageService)

}
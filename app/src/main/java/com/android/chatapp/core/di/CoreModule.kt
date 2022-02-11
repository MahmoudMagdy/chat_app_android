package com.android.chatapp.core.di

import android.content.Context
import com.android.chatapp.core.data.local.ChatAppDatabase
import com.android.chatapp.core.data.remote.RemoteDataSource
import com.android.chatapp.core.data.remote.RemoteDataSourceImpl
import com.android.chatapp.core.data.repository.StorageRepositoryImpl
import com.android.chatapp.core.domain.repository.StorageRepository
import com.android.chatapp.feature_authentication.data.provider.user.TokensProvider
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
    fun provideLocalDatabase(@ApplicationContext context: Context): ChatAppDatabase {
        return ChatAppDatabase(context)
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(tokensProvider: TokensProvider): RemoteDataSource {
        return RemoteDataSourceImpl(tokensProvider)
    }

    @Provides
    @Singleton
    fun provideStorageRepository(remoteDataSource: RemoteDataSource): StorageRepository =
        StorageRepositoryImpl(remoteDataSource.storageService)

}
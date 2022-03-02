package com.android.chatapp.feature_authentication.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.android.chatapp.TokenPreferences
import com.android.chatapp.core.data.local.ChatAppDatabase
import com.android.chatapp.core.data.remote.RemoteDataSource
import com.android.chatapp.feature_authentication.data.provider.token.TokenProvider
import com.android.chatapp.feature_authentication.data.provider.token.TokenProviderImpl
import com.android.chatapp.feature_authentication.data.provider.token.serializer.TokenPreferencesSerializer
import com.android.chatapp.feature_authentication.data.provider.token.util.TOKEN_STORE_FILE_NAME
import com.android.chatapp.feature_authentication.data.provider.reset_password.ResetPasswordProvider
import com.android.chatapp.feature_authentication.data.provider.reset_password.ResetPasswordProviderImpl
import com.android.chatapp.feature_authentication.data.repository.AuthRepositoryImpl
import com.android.chatapp.feature_authentication.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AuthenticationModule {

    @Provides
    @Singleton
    fun provideTokenDataStore(@ApplicationContext context: Context): DataStore<TokenPreferences> =
        DataStoreFactory.create(
            serializer = TokenPreferencesSerializer,
            produceFile = { context.dataStoreFile(TOKEN_STORE_FILE_NAME) },
            corruptionHandler = ReplaceFileCorruptionHandler(produceNewData = { TokenPreferences.getDefaultInstance() }),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        )

    @Provides
    @Singleton
    fun provideTokenProvider(dataStore: DataStore<TokenPreferences>): TokenProvider =
        TokenProviderImpl(store = dataStore)


    @Provides
    @Singleton
    fun provideResetPasswordProvider(@ApplicationContext context: Context): ResetPasswordProvider =
        ResetPasswordProviderImpl(context = context)


    @Provides
    @Singleton
    fun provideAuthRepository(
        remoteDataSource: RemoteDataSource, db: ChatAppDatabase,
        tokenProvider: TokenProvider, resetPasswordProvider: ResetPasswordProvider
    ): AuthRepository =
        AuthRepositoryImpl(
            service = remoteDataSource.authService,
            dao = db.userDao(),
            profileDao = db.profileDao(),
            provider = tokenProvider,
            resetProvider = resetPasswordProvider
        )
}
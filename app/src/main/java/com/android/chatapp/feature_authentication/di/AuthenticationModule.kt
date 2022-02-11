package com.android.chatapp.feature_authentication.di

import android.content.Context
import com.android.chatapp.core.data.local.ChatAppDatabase
import com.android.chatapp.core.data.remote.RemoteDataSource
import com.android.chatapp.feature_authentication.data.provider.reset_password.ResetPasswordProvider
import com.android.chatapp.feature_authentication.data.provider.reset_password.ResetPasswordProviderImpl
import com.android.chatapp.feature_authentication.data.provider.user.TokensProvider
import com.android.chatapp.feature_authentication.data.provider.user.TokensProviderImpl
import com.android.chatapp.feature_authentication.data.repository.AuthRepositoryImpl
import com.android.chatapp.feature_authentication.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthenticationModule {

    @Provides
    @Singleton
    fun provideTokensProvider(@ApplicationContext context: Context): TokensProvider =
        TokensProviderImpl(context = context)


    @Provides
    @Singleton
    fun provideResetPasswordProvider(@ApplicationContext context: Context): ResetPasswordProvider =
        ResetPasswordProviderImpl(context = context)


    @Provides
    @Singleton
    fun provideAuthRepository(
        remoteDataSource: RemoteDataSource, db: ChatAppDatabase,
        tokensProvider: TokensProvider, resetPasswordProvider: ResetPasswordProvider
    ): AuthRepository =
        AuthRepositoryImpl(
            service = remoteDataSource.authService,
            dao = db.userDao(),
            profileDao = db.profileDao(),
            provider = tokensProvider,
            resetProvider = resetPasswordProvider
        )
}
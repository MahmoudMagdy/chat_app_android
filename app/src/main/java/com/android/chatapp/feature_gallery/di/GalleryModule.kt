package com.android.chatapp.feature_gallery.di

import android.content.Context
import com.android.chatapp.feature_gallery.data.provider.buckets.BucketsProvider
import com.android.chatapp.feature_gallery.data.provider.buckets.BucketsProviderImpl
import com.android.chatapp.feature_gallery.data.repository.LocalStorageRepositoryImpl
import com.android.chatapp.feature_gallery.domain.repository.LocalStorageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GalleryModule {

    @Provides
    @Singleton
    fun provideBucketsProvider(@ApplicationContext context: Context): BucketsProvider =
        BucketsProviderImpl(context)

    @Provides
    @Singleton
    fun provideLocalStorageRepository(
        @ApplicationContext context: Context,
        bucketsProvider: BucketsProvider
    ): LocalStorageRepository =
        LocalStorageRepositoryImpl(context, bucketsProvider)
}
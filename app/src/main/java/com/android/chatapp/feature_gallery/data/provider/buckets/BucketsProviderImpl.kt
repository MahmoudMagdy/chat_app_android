package com.android.chatapp.feature_gallery.data.provider.buckets

import android.content.Context
import android.content.SharedPreferences


class BucketsProviderImpl(context: Context) : BucketsProvider {
    companion object {
        private const val PREFERENCE_NAME = "buckets_preference"
        private const val IMAGES_DOWNLOAD_BUCKET_TAG = "images_download_bucket"
        private const val VIDEOS_DOWNLOAD_BUCKET_TAG = "videos_download_bucket"
        private const val BUCKET_DEFAULT_VALUE = -1L
    }

    private val appContext = context.applicationContext
    private val preferences: SharedPreferences = appContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    override fun setVideosDownloadBucket(bucket: Long) {
        preferences.edit().putLong(VIDEOS_DOWNLOAD_BUCKET_TAG, bucket).apply()
    }

    override fun setImagesDownloadBucket(bucket: Long) {
        preferences.edit().putLong(IMAGES_DOWNLOAD_BUCKET_TAG, bucket).apply()
    }

    override val videosDownloadBucket get() = preferences.getLong(VIDEOS_DOWNLOAD_BUCKET_TAG, BUCKET_DEFAULT_VALUE)

    override val imagesDownloadBucket get() = preferences.getLong(IMAGES_DOWNLOAD_BUCKET_TAG, BUCKET_DEFAULT_VALUE)

    override val isVideosDownloadBucketExists get() = preferences.contains(VIDEOS_DOWNLOAD_BUCKET_TAG)

    override val isImagesDownloadBucketExists get() = preferences.contains(IMAGES_DOWNLOAD_BUCKET_TAG)

    override val downloadBuckets
        get() = preferences.run {
            listOf(
                getLong(VIDEOS_DOWNLOAD_BUCKET_TAG, BUCKET_DEFAULT_VALUE),
                getLong(IMAGES_DOWNLOAD_BUCKET_TAG, BUCKET_DEFAULT_VALUE)
            ).filter { it != BUCKET_DEFAULT_VALUE }
        }

    override fun deleteDownloadBuckets() {
        preferences.edit()
            .remove(VIDEOS_DOWNLOAD_BUCKET_TAG)
            .remove(IMAGES_DOWNLOAD_BUCKET_TAG)
            .apply()
    }
}
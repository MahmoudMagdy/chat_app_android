package com.android.chatapp.feature_gallery.data.provider.buckets

interface BucketsProvider {
    val videosDownloadBucket: Long
    val imagesDownloadBucket: Long
    val isVideosDownloadBucketExists: Boolean
    val isImagesDownloadBucketExists: Boolean
    val downloadBuckets: List<Long>
    fun setVideosDownloadBucket(bucket: Long)
    fun setImagesDownloadBucket(bucket: Long)
    fun deleteDownloadBuckets()
}
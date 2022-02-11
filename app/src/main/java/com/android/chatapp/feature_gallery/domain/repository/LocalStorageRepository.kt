package com.android.chatapp.feature_gallery.domain.repository

import android.content.IntentSender
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_gallery.domain.model.*
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.InputStream

interface LocalStorageRepository {
    val retrievedDownloadedFiles: LiveData<List<LocalStory>>

    fun insertImageFileAtLocation(name: String, dir: String, file: File): LocalFile?
    fun insertImageBitmapAtLocation(name: String, dir: String, bitmap: Bitmap): Flow<Resource<LocalImage, Nothing>>
    fun insertVideoFileAtLocation(name: String, dir: String, file: File): LocalFile?
    fun getImageNameAndType(uri: Uri): Pair<String, String>?
    fun scanNewFile(paths: Array<String>)
    fun openInputStream(uri: Uri): InputStream?
    suspend fun deleteFile(
        uri: Uri,
        onSuccessListener: () -> Unit,
        onErrorListener: (RuntimeException) -> Unit,
        onRequestAction: (IntentSender) -> Unit
    )

    fun getImageFolders(): Flow<Resource<List<MediaFolder>, Nothing>>
    fun getImagesByFolder(bucketId: Long): Flow<Resource<List<LocalImage>, Nothing>>
    fun getVideoFolders(): Flow<Resource<List<MediaFolder>, Nothing>>
    fun getVideosByFolder(bucketId: Long): Flow<Resource<List<LocalVideo>, Nothing>>
    fun getDownloadedMedia(): LiveData<List<LocalStory>>
}
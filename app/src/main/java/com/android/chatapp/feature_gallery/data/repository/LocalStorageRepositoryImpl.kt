package com.android.chatapp.feature_gallery.data.repository

import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.IntentSender
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.chatapp.core.domain.util.PermissionsUtils
import com.android.chatapp.core.domain.util.ERROR_TAG
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_gallery.data.provider.buckets.BucketsProvider
import com.android.chatapp.feature_gallery.data.util.ContentUriNotCreated
import com.android.chatapp.feature_gallery.domain.model.*
import com.android.chatapp.feature_gallery.domain.repository.LocalStorageRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

class LocalStorageRepositoryImpl(context: Context, private val bucketsProvider: BucketsProvider) :
    LocalStorageRepository {

    companion object {
        const val DIRECTORY_CAMERA_STORIES = "DCIM/ChatApp/Camera/Stories"
        const val DIRECTORY_CAMERA_PROFILE_PICTURES = "DCIM/ChatApp/Camera/ProfilePictures"
        const val DIRECTORY_CAMERA_PROFILE_COVERS = "DCIM/ChatApp/Camera/ProfileCovers"
        const val DIRECTORY_PROFILE_PICTURES = "Pictures/ChatApp/ProfilePictures"
        const val DIRECTORY_PROFILE_COVERS = "Pictures/ChatApp/ProfileCovers"
        const val DIRECTORY_IMAGES_STORIES = "Pictures/ChatApp/Stories"
        const val DIRECTORY_VIDEO_STORIES = "Movies/ChatApp/Stories"
        val isApiQorHigher get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }

    private val appContext = context.applicationContext
    private val contentResolver = context.contentResolver

    private val retrievedImageFolders: MutableList<MediaFolder> = mutableListOf()

    private val retrievedVideoFolders: MutableList<MediaFolder> = mutableListOf()

    private val _retrievedDownloadedFiles = MutableLiveData<List<LocalStory>>()
    override val retrievedDownloadedFiles: LiveData<List<LocalStory>>
        get() = _retrievedDownloadedFiles


    /**
     * IMPORTANT!!
     * name: is the name of the file and must be unique in api <= 28 (api P)
     */
    override fun insertImageFileAtLocation(name: String, dir: String, file: File): LocalFile? {
        if (file.exists() && file.length() != 0L) {
            // Add a media item that other apps shouldn't see until the item is
            // fully written to the media store.

            // Find all images files on the primary external storage device.
            // On API <= 28, use VOLUME_EXTERNAL instead.
            val imagesCollection = MediaStore.Images.Media
                .getContentUri(if (isApiQorHigher) MediaStore.VOLUME_EXTERNAL_PRIMARY else MediaStore.VOLUME_EXTERNAL)

            var path: String? = null
            val imageDetails = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, name)
                if (isApiQorHigher) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, dir)
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                } else {
                    val imgFile =
                        File(Environment.getExternalStoragePublicDirectory(""), "$dir/$name")
                    val parentFile = imgFile.parentFile
                    if (parentFile != null && !parentFile.exists()) parentFile.mkdirs()
                    if (!imgFile.exists() || imgFile.length() == 0L) imgFile.createNewFile()
                    path = imgFile.path
                    put(MediaStore.Images.Media.DATA, imgFile.absolutePath)
                }
            }

            val imageContentUri = contentResolver.insert(imagesCollection, imageDetails)

            if (imageContentUri != null) {
                contentResolver.openFileDescriptor(imageContentUri, "w", null).use { pfd ->
                    ParcelFileDescriptor.AutoCloseOutputStream(pfd).write(file.readBytes())
                }
                // Now that we're finished, release the "pending" status, and allow other apps
                // to display the image.
                if (isApiQorHigher) {
                    imageDetails.clear()
                    imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
                    contentResolver.update(imageContentUri, imageDetails, null, null)
                } else scanNewFile(arrayOf(path!!))
                if (!bucketsProvider.isImagesDownloadBucketExists && dir == DIRECTORY_IMAGES_STORIES)
                    getFileBucketId(imageContentUri)?.also {
                        bucketsProvider.setImagesDownloadBucket(
                            it
                        )
                    }
                return LocalFile(name, imageContentUri, file.length(), "$dir/$name")
            }
        }
        return null
    }

    /**
     * IMPORTANT!!
     * name: is the name of the file and must be unique in api <= 28 (api P)
     */
    override fun insertImageBitmapAtLocation(
        name: String,
        dir: String,
        bitmap: Bitmap
    ): Flow<Resource<LocalImage, Nothing>> = flow {
        // Add a media item that other apps shouldn't see until the item is
        // fully written to the media store.
        emit(Resource.Loading())
        // Find all images files on the primary external storage device.
        // On API <= 28, use VOLUME_EXTERNAL instead.
        val imagesCollection = MediaStore.Images.Media
            .getContentUri(
                if (isApiQorHigher) MediaStore.VOLUME_EXTERNAL_PRIMARY
                else MediaStore.VOLUME_EXTERNAL
            )

        var path: String? = null
        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$name.jpeg")
            if (isApiQorHigher) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, dir)
                put(MediaStore.Images.Media.IS_PENDING, 1)
            } else {
                val file =
                    File(Environment.getExternalStoragePublicDirectory(""), "$dir/$name.jpeg")
                val parentFile = file.parentFile
                if (parentFile != null && !parentFile.exists()) parentFile.mkdirs()
                val result = runCatching {
                    if (!file.exists() || file.length() == 0L) file.createNewFile()
                }
                if (result.isFailure) {
                    emit(Resource.Error(result.exceptionOrNull() ?: UnknownError()))
                    return@flow
                }
                path = file.path
                put(MediaStore.Images.Media.DATA, file.absolutePath)
            }
        }

        val imageContentUri = contentResolver.insert(imagesCollection, imageDetails)

        if (imageContentUri != null) {
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val result = runCatching {
                contentResolver.openFileDescriptor(imageContentUri, "w", null).use { pfd ->
                    ParcelFileDescriptor.AutoCloseOutputStream(pfd).write(bytes.toByteArray())
                }
            }
            if (result.isFailure) {
                emit(Resource.Error(result.exceptionOrNull() ?: UnknownError()))
                return@flow
            }
            // Now that we're finished, release the "pending" status, and allow other apps
            // to display the image.
            if (isApiQorHigher) {
                imageDetails.clear()
                imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(imageContentUri, imageDetails, null, null)
            } else scanNewFile(arrayOf(path!!))
            emit(
                Resource.Success(
                    LocalImage(
                        name = name,
                        id = getFileBucketId(imageContentUri) ?: -1,
                        uri = imageContentUri,
                        size = bytes.size().toLong(),
                        mimeType = "image/jpeg"
                    )
                )
            )

        } else emit(Resource.Error(ContentUriNotCreated()))
    }

    /**
     * IMPORTANT!!
     * name: is the name of the file and must be unique in api <= 28 (api P)
     */
    override fun insertVideoFileAtLocation(name: String, dir: String, file: File): LocalFile? {
        if (file.exists() && file.length() != 0L) {
            // Add a media item that other apps shouldn't see until the item is
            // fully written to the media store.

            // Find all video files on the primary external storage device.
            // On API <= 28, use VOLUME_EXTERNAL instead.
            val videoCollection = MediaStore.Video.Media
                .getContentUri(if (isApiQorHigher) MediaStore.VOLUME_EXTERNAL_PRIMARY else MediaStore.VOLUME_EXTERNAL)
            var path: String? = null
            val videoDetails = ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, name)
                if (isApiQorHigher) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, dir)
                    put(MediaStore.Video.Media.IS_PENDING, 1)
                } else {
                    val videoFile =
                        File(Environment.getExternalStoragePublicDirectory(""), "$dir/$name")
                    val parentFile = videoFile.parentFile
                    if (parentFile != null && !parentFile.exists()) parentFile.mkdirs()
                    if (!videoFile.exists() || videoFile.length() == 0L) videoFile.createNewFile()
                    path = videoFile.path
                    put(MediaStore.Images.Media.DATA, videoFile.absolutePath)
                }
            }
            val videoContentUri = contentResolver.insert(videoCollection, videoDetails)
            if (videoContentUri != null) {
                contentResolver.openFileDescriptor(videoContentUri, "w", null).use { pfd ->
                    ParcelFileDescriptor.AutoCloseOutputStream(pfd).write(file.readBytes())
                }
                // Now that we're finished, release the "pending" status, and allow other apps
                // to play the video track.
                if (isApiQorHigher) {
                    videoDetails.clear()
                    videoDetails.put(MediaStore.Video.Media.IS_PENDING, 0)
                    contentResolver.update(videoContentUri, videoDetails, null, null)
                } else scanNewFile(arrayOf(path!!))
                if (!bucketsProvider.isVideosDownloadBucketExists)
                    getFileBucketId(videoContentUri)?.also {
                        bucketsProvider.setVideosDownloadBucket(it)
                    }
                return LocalFile(name, videoContentUri, file.length(), "$dir/$name")
            }
        }
        return null
    }

    override suspend fun deleteFile(
        uri: Uri, onSuccessListener: () -> Unit, onErrorListener: (RuntimeException) -> Unit,
        onRequestAction: (IntentSender) -> Unit
    ) {
        try {
            contentResolver.delete(uri, null, null)
            GlobalScope.launch(Dispatchers.Main) {
                onSuccessListener()
            }
        } catch (securityException: SecurityException) {
            if (isApiQorHigher) {
                val recoverableSecurityException = securityException as?
                        RecoverableSecurityException
                val intentSender =
                    recoverableSecurityException?.userAction?.actionIntent?.intentSender
                GlobalScope.launch(Dispatchers.Main) {
                    intentSender?.also(onRequestAction)
                        ?: onErrorListener(
                            RuntimeException(
                                securityException.message,
                                securityException
                            )
                        )
                }
            } else GlobalScope.launch(Dispatchers.Main) {
                onErrorListener(RuntimeException(securityException.message, securityException))
            }
        } catch (ex: Exception) {
            GlobalScope.launch(Dispatchers.Main) {
                onErrorListener(RuntimeException(ex.message, ex))
            }
        }
    }

    override fun scanNewFile(paths: Array<String>) {
        //val msc = MediaScannerConnection()
        MediaScannerConnection.scanFile(appContext, paths, null, null)
    }

    override fun openInputStream(uri: Uri): InputStream? = contentResolver.openInputStream(uri)

    private fun getFileBucketId(uri: Uri): Long? {
        val projection = arrayOf(MediaStore.MediaColumns.BUCKET_ID)
        if (PermissionsUtils.isExternalStorageReadable()) {
            val cursor = contentResolver.query(uri, projection, null, null, null)
            try {
                cursor?.apply {
                    if (moveToFirst())
                        return getLong(getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID))
                    close()
                }
            } catch (ex: Exception) {
            }
        }
        return null
    }

    override fun getImageNameAndType(uri: Uri): Pair<String, String>? {
        val projection = arrayOf(
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE
        )
        if (PermissionsUtils.isExternalStorageReadable()) {
            val cursor = contentResolver.query(uri, projection, null, null, null)
            try {
                cursor?.apply {
                    if (moveToFirst()) {
                        val displayName =
                            getString(getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                        val mimeType =
                            getString(getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE))
                        val type = mimeType.removePrefix("image/")
                        val pos = displayName.lastIndexOf(".")
                        val name =
                            if (pos > 0 && pos < displayName.length - 1) { // If '.' is not the first or last character.
                                displayName.substring(0, pos)
                            } else displayName.substring(0, pos)
                        return Pair(name, type)
                    }
                    close()
                }
            } catch (ex: Exception) {
            }
        }
        return null
    }

    override fun getVideoFolders() =
        getMediaFolders(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, retrievedVideoFolders)

    override fun getImageFolders() =
        getMediaFolders(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, retrievedImageFolders)

    private fun getMediaFolders(allMediaUri: Uri, retrievedMediaFolders: MutableList<MediaFolder>) =
        flow {
            emit(Resource.Loading())
            if (retrievedMediaFolders.isNotEmpty()) emit(Resource.Success(retrievedMediaFolders))
            val mediaFolders = mutableListOf<MediaFolder>()
            val mediaFoldersId = mutableListOf<Long>()
            val projection = arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
                MediaStore.MediaColumns.BUCKET_ID
            )
            //val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
            if (PermissionsUtils.isExternalStorageReadable()) {
                val cursor = contentResolver.query(allMediaUri, projection, null, null, null)
                try {
                    cursor?.apply {
                        if (moveToFirst())
                            do {
                                val id = getLong(getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                                val folder =
                                    getString(getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME))
                                val bucketId =
                                    getLong(getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID))
                                val contentUri: Uri = ContentUris.withAppendedId(allMediaUri, id)
                                if (bucketId !in mediaFoldersId) {
                                    mediaFoldersId += bucketId
                                    val mediaFolder = MediaFolder(
                                        folder,
                                        bucketId,
                                        contentUri /*if the folder has only one picture this line helps to set it as first so as to avoid blank image in itemview*/
                                    )
                                    mediaFolder.increaseMediaNumber()
                                    mediaFolders += mediaFolder
                                } else {
                                    mediaFolders.find { it.bucketId == bucketId }?.apply {
                                        setFirstMediaUri(contentUri)
                                        increaseMediaNumber()
                                    }
                                }

                            } while (moveToNext())
                    }
                } catch (ex: Exception) {
                    emit(Resource.Error(ex))
                } finally {
                    cursor?.close()
                }
            }
            retrievedMediaFolders.clear()
            retrievedMediaFolders.addAll(mediaFolders)
            emit(Resource.Success(retrievedMediaFolders))
        }

    override fun getVideosByFolder(bucketId: Long) = flow {
        emit(Resource.Loading())
        val videos = mutableListOf<LocalVideo>()
        val allVideosUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DURATION
        )
        val selection = "${MediaStore.Video.Media.BUCKET_ID} = ?"
        if (PermissionsUtils.isExternalStorageReadable()) {
            val cursor = contentResolver.query(
                allVideosUri,
                projection,
                selection,
                arrayOf(bucketId.toString()),
                null
            )
            try {
                cursor?.apply {
                    if (moveToFirst())
                        do {
                            val id = getLong(getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                            val name =
                                getString(getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))
                            val size =
                                getLong(getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))
                            val mimeType =
                                getString(getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE))
                            val duration =
                                getLong(getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                            val contentUri: Uri = ContentUris.withAppendedId(allVideosUri, id)
                            val video =
                                LocalVideo(duration, name, id, contentUri, size, mimeType)
                            videos += video
                        } while (moveToNext())
                    close()
                }
            } catch (ex: Exception) {
                emit(Resource.Error(ex))
            }
        }
        videos.reverse()
        emit(Resource.Success(videos))
    }

    override fun getImagesByFolder(bucketId: Long) = flow {
        emit(Resource.Loading())
        val images = mutableListOf<LocalImage>()
        val allImagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.MIME_TYPE
        )
        val selection = "${MediaStore.Images.Media.BUCKET_ID} = ?"
        if (PermissionsUtils.isExternalStorageReadable()) {
            val cursor = contentResolver.query(
                allImagesUri,
                projection,
                selection,
                arrayOf(bucketId.toString()),
                null
            )
            try {
                cursor?.apply {
                    if (moveToFirst())
                        do {
                            val id = getLong(getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                            val name =
                                getString(getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                            val size =
                                getLong(getColumnIndexOrThrow(MediaStore.Images.Media.SIZE))
                            val mimeType =
                                getString(getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE))
                            val contentUri: Uri = ContentUris.withAppendedId(allImagesUri, id)
                            val image = LocalImage(name, id, contentUri, size, mimeType)
                            images += image
                        } while (moveToNext())
                }
            } catch (ex: Exception) {
                emit(Resource.Error(ex))
            } finally {
                cursor?.close()
            }
        }
        images.reverse()
        emit(Resource.Success(images))
    }

    override fun getDownloadedMedia(): LiveData<List<LocalStory>> {
        GlobalScope.launch(Dispatchers.IO) {
            mutableListOf<LocalStory>().apply {
                val imagesBucket = bucketsProvider.imagesDownloadBucket
                var images: Deferred<List<LocalStory>>? = null
                if (imagesBucket != -1L)
                    images = async {
                        getMediaStory(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            imagesBucket,
                            DIRECTORY_IMAGES_STORIES,
                            "image/",
                            ContentTypes.IMAGE_STORY
                        )
                    }


                val videosBucket = bucketsProvider.videosDownloadBucket
                var videos: Deferred<List<LocalStory>>? = null
                if (videosBucket != -1L)
                    videos = async {
                        getMediaStory(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            videosBucket,
                            DIRECTORY_VIDEO_STORIES,
                            "video/",
                            ContentTypes.VIDEO_STORY
                        )
                    }
                addAll((images?.await() ?: listOf()) + (videos?.await() ?: listOf()))
                sortByDescending { it.modified }
                _retrievedDownloadedFiles.postValue(this)
            }
        }
        return retrievedDownloadedFiles
    }

    private suspend fun getMediaStory(
        mediaUri: Uri,
        bucketId: Long,
        dir: String,
        prefix: String,
        type: Int
    ): List<LocalStory> {
        val mediaFiles = mutableListOf<LocalStory>()
        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.DATE_MODIFIED
        )
        val selection = "${MediaStore.MediaColumns.BUCKET_ID} = ?"
        if (PermissionsUtils.isExternalStorageReadable()) {
            val cursor = contentResolver.query(
                mediaUri,
                projection,
                selection,
                arrayOf(bucketId.toString()),
                null
            )
            try {
                cursor?.apply {
                    if (moveToFirst())
                        do {
                            val id = getLong(getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                            val name =
                                getString(getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
                            val size =
                                getLong(getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE))
                            val dateModified =
                                getLong(getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED)) * 1000
                            val mimeType =
                                getString(getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE))
//                            val ext = mimeType.removePrefix(prefix)
                            val contentUri: Uri = ContentUris.withAppendedId(
                                mediaUri,
                                id
                            )
                            val mediaFile = LocalStory(
                                contentUri,
                                File(
                                    Environment.getExternalStoragePublicDirectory(""),
                                    "$dir/$name"
                                ).path,
                                name, mimeType, size, dateModified, type
                            )
                            mediaFiles += mediaFile
                        } while (moveToNext())
                    close()
                }
            } catch (ex: Exception) {
                Log.e(ERROR_TAG, ex.message, ex)
            }
        }
        return mediaFiles
    }

}



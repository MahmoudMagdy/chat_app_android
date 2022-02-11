package com.android.chatapp.feature_gallery.domain.model

import android.net.Uri
import android.os.Parcelable
import android.text.format.DateUtils
import com.android.chatapp.core.domain.util.AppFilesUtil
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocalStory(val uri: Uri, val path: String, val name: String, val extension: String, val size: Long, val modified: Long, val type: Int) : Parcelable {
    val saveName
        get() = name.length.let {
            val div = (it.toFloat() / STORY_NAME.length) / 2
            if (div < 1) "${STORY_NAME}_${name}_${AppFilesUtil.nameSuffix}.$extension"
            else "${name.replaceRange(0 until (STORY_NAME.length * div).toInt(), "${STORY_NAME}_")}_${AppFilesUtil.nameSuffix}.$extension"
        }

    val dateAndSize get() = "${DateUtils.getRelativeTimeSpanString(modified)}   ${AppFilesUtil.formatSize(size)}"


    override fun toString(): String = "uri: $uri,  path:$path ,  name: $name,  extension: $extension,  modified: $modified,  type: $type"

    companion object {
        private const val STORY_NAME = "story"
        val IMAGES_EXTENSIONS = arrayOf("jpg", "png", "jpeg", "jpe", "jpe", "jfif", "jfi", "gif", "tif", "tiff", "webp", "bmp", "dib")
        val VIDEOS_EXTENSIONS = arrayOf("mp4", "m4a", "m4v", "f4v", "f4a", "m4b", "m4r", "f4b", "mov", "ogg", "oga", "ogv", "ogx", "webm", "ts", "mpg", "mpeg", "m2p", "ps")
    }

}
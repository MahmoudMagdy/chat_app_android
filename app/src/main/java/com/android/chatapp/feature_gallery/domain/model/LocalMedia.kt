package com.android.chatapp.feature_gallery.domain.model

import android.net.Uri
import android.os.Parcelable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.android.chatapp.core.domain.util.AppFilesUtil
import com.android.chatapp.core.presentation.util.UriAsStringSerializer
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
@Parcelize
open class LocalMedia(
    val name: String,
    val id: Long,
    @Serializable(with = UriAsStringSerializer::class)
    val uri: Uri,
    val size: Long,
    val mimeType: String
) : Parcelable {
    @IgnoredOnParcel
    var selected by mutableStateOf(false)
        private set

    val select get() = run { selected = true }
    val unselect get() = run { selected = false }

    val sizeMB get() = size / (1024f * 1024)
    val sizeStr get() = AppFilesUtil.formatSize(size)
    val extension
        get() = name.substringAfterLast('.', "").ifBlank { mimeType.removePrefix("image/") }
    val firstNameSegment: String
        get() = name.run {
            val position = lastIndexOf(".")
            if (position <= 0)
            // If '.' is the first character.
                UUID.randomUUID().toString()
            else if (position == length - 1)
            // If '.' is last character.
                substring(0, position)
            else substring(0, position)
        }

    override fun toString(): String =
        "NAME = $name,\tID = $id,\tSIZE = $size,\tURI = $uri,\tEXT = $mimeType,\tSELECTED = $selected"
}
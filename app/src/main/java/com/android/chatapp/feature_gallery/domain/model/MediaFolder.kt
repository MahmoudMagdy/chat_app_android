package com.android.chatapp.feature_gallery.domain.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Parcelize
data class MediaFolder(
    val name: String,
    val bucketId: Long,
    @Transient
    private var _firstMediaUri: Uri = Uri.EMPTY,
    private var mediaNumber: Int = 0
) : Parcelable {

    val firstMediaUri get() = _firstMediaUri

    fun increaseMediaNumber() {
        mediaNumber++
    }

    fun setFirstMediaUri(uri: Uri) {
        _firstMediaUri = uri
    }

    fun sizeStr(suffix: String) = "$mediaNumber $suffix"
}
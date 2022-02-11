package com.android.chatapp.feature_gallery.domain.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.android.chatapp.core.domain.util.AppFilesUtil

class LocalVideo(val duration: Long, name: String, id: Long, uri: Uri, size: Long, mimeType: String) : LocalMedia(name, id, uri, size, mimeType),
    Parcelable {
    val durationStr: String get() = AppFilesUtil.formatMilliseconds(duration)

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readParcelable(Uri::class.java.classLoader)!!,
        parcel.readLong(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(duration)
        super.writeToParcel(parcel, flags)
    }

    override fun describeContents(): Int = 0

    override fun toString(): String =
        "NAME = $name,\tID = $id,\tSIZE = $size,\tURI = $uri,\tEXT = $mimeType,\tDURATION = $duration,\tSELECTED = $selected"

    companion object CREATOR : Parcelable.Creator<LocalVideo> {
        override fun createFromParcel(parcel: Parcel): LocalVideo = LocalVideo(parcel)

        override fun newArray(size: Int): Array<LocalVideo?> = arrayOfNulls(size)
    }
}
package com.android.chatapp.feature_gallery.domain.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.serialization.Serializable

class LocalImage(name: String, id: Long, uri: Uri, size: Long, mimeType: String) : LocalMedia(name, id, uri, size, mimeType), Parcelable
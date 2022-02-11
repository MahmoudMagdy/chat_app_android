package com.android.chatapp.feature_gallery.presentation.util

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class GallerySettings(
    val cropMedia: Boolean,
    val cropType: Int,
    val gallery: Gallery = Gallery.IMAGE
) : Parcelable
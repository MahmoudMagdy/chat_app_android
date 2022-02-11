package com.android.chatapp.feature_gallery.domain.model

import android.net.Uri

data class LocalFile(val name: String, val uri: Uri, val size: Long, val path: String, val dateAdded: Long? = null)
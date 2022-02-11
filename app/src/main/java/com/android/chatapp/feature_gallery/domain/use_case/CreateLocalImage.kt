package com.android.chatapp.feature_gallery.domain.use_case

import android.net.Uri
import com.android.chatapp.feature_gallery.domain.model.LocalImage
import java.io.File
import javax.inject.Inject

class CreateLocalImage @Inject constructor() {
    operator fun invoke(uri: Uri): LocalImage {
        val file = File(uri.path!!)
        val name = uri.lastPathSegment!!
        val ext = name.substringAfterLast('.', "").ifBlank { file.extension }
        return LocalImage(name, -1, uri, file.length(), "image/$ext")
    }
}


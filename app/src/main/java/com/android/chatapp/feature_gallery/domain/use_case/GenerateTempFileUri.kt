package com.android.chatapp.feature_gallery.domain.use_case

import android.net.Uri
import com.android.chatapp.feature_gallery.domain.model.LocalMedia
import java.io.File
import javax.inject.Inject

class GenerateTempFileUri @Inject constructor() {
    operator fun invoke(media: LocalMedia): Uri {
        val tempFile = File.createTempFile(media.firstNameSegment, ".${media.extension}")
        return Uri.fromFile(tempFile)
    }
}


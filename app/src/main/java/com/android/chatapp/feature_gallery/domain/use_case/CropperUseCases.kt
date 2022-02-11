package com.android.chatapp.feature_gallery.domain.use_case

import javax.inject.Inject

class CropperUseCases @Inject constructor(
    val createLocalImage: CreateLocalImage,
    val generateTempFileUri: GenerateTempFileUri
)


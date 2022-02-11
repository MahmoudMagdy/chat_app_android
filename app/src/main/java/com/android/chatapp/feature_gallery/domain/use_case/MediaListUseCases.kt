package com.android.chatapp.feature_gallery.domain.use_case

import javax.inject.Inject

class MediaListUseCases @Inject constructor(
    val getMedia: GetMedia,
    val resourceToUiState: ResourceToUiState
)


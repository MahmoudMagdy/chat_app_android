package com.android.chatapp.feature_gallery.domain.use_case

import javax.inject.Inject

class FolderListUseCases @Inject constructor(
    val getFolders: GetFolders,
    val resourceToUiState: ResourceToUiState
)


package com.android.chatapp.feature_gallery.presentation.media_list

import com.android.chatapp.feature_gallery.domain.model.LocalMedia

sealed class MediaListEvent {
    data class MediaChosen(val media: LocalMedia) : MediaListEvent()
}

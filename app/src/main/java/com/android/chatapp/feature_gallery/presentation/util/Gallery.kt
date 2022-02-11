package com.android.chatapp.feature_gallery.presentation.util

import androidx.annotation.StringRes
import com.android.chatapp.R

enum class Gallery(@StringRes val title: Int) {
    IMAGE(R.string.media_gallery_photos_title),
    VIDEO(R.string.media_gallery_videos_title);
}

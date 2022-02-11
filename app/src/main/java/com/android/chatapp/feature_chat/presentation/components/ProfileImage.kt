package com.android.chatapp.feature_chat.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import coil.compose.rememberImagePainter
import com.android.chatapp.R
import com.android.chatapp.core.presentation.util.EMPTY_TEXT
import com.android.chatapp.feature_authentication.domain.model.Profile
import com.android.chatapp.feature_authentication.domain.model.name

@Composable
fun ProfileImage(
    modifier: Modifier = Modifier,
    profile: Profile?,
    size: Dp,
    shape: Shape,
    onClick: (() -> Unit)? = null
) {
    Image(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(Color.LightGray)
            .apply { if (onClick != null) clickable(onClick = onClick) },
        painter = rememberImagePainter(
            data = profile?.image?.media,
            builder = { crossfade(true) }
        ),
        contentScale = ContentScale.Crop,
        contentDescription =
        stringResource(id = R.string.chat_feature_pro_pic_desc, profile?.name ?: EMPTY_TEXT)
    )
}
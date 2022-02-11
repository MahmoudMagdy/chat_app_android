package com.android.chatapp.feature_authentication.presentation.user_info.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.android.chatapp.R
import com.android.chatapp.feature_gallery.domain.model.LocalMedia

sealed class ProfilePhotoSource {
    data class Loading(@DrawableRes val id: Int) : ProfilePhotoSource()
    data class Resource(@DrawableRes val id: Int) : ProfilePhotoSource()
    data class Media(val media: LocalMedia) : ProfilePhotoSource()
}


@Composable
fun ProfilePhotoInput(
    modifier: Modifier = Modifier,
    src: ProfilePhotoSource,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier.size(164.dp)
    ) {
        if (src !is ProfilePhotoSource.Loading)
            Image(
                painter = when (src) {
                    is ProfilePhotoSource.Media -> rememberImagePainter(
                        data = src.media.uri,
                        builder = {
                            crossfade(true)
                        }
                    )
                    is ProfilePhotoSource.Resource -> painterResource(id = src.id)
                    is ProfilePhotoSource.Loading -> painterResource(id = src.id)
                },
                contentDescription = stringResource(id = R.string.user_info_profile_img_desc),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        else
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.LightGray, shape = CircleShape)
            )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick)
                .background(color = Color.Black.copy(alpha = 0.3f), shape = CircleShape)
        )
        if (src !is ProfilePhotoSource.Loading)
            TextButton(
                modifier = Modifier.align(Alignment.Center),
                onClick = onClick,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        modifier = Modifier.size(42.dp),
                        painter = painterResource(id = R.drawable.user_info_choose_img_ic),
                        contentDescription = stringResource(id = R.string.user_info_choose_img_desc)
                    )
                    Text(
                        text = stringResource(id = R.string.user_info_choose_photo_title),
                        fontSize = 16.sp
                    )
                }
            }
        else CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .size(24.dp)
        )
    }
}

package com.android.chatapp.feature_chat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.android.chatapp.R

@Composable
fun UserOnlineIndicator(modifier: Modifier) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .background(Color.Green, RoundedCornerShape(5.dp))
//            .border(1.dp, MaterialTheme.colors.onSurface, RoundedCornerShape(5.dp))
            .size(14.dp)
            .semantics {
                contentDescription = context.getString(R.string.chat_list_user_online)
            },
    )
}
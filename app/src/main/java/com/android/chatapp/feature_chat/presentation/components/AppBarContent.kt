package com.android.chatapp.feature_chat.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.chatapp.R

@Composable
fun AppBarContent(onLogoutClick: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
    ) {
        if (onLogoutClick != null)
            IconButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = onLogoutClick
            ) {
                Icon(
                    imageVector = Icons.Outlined.Logout,
                    contentDescription = stringResource(id = R.string.chat_list_logout_btn_desc)
                )
            }
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.h6.copy(fontSize = 18.sp)
        )
    }
}

package com.android.chatapp.feature_gallery.presentation.cropper.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.android.chatapp.core.presentation.util.spacing
import java.util.*

@Composable
fun WhiteTextButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    TextButton(
        modifier = modifier
            .padding(horizontal = MaterialTheme.spacing.medium),
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
    ) {
        Text(
            text = text.uppercase(Locale.getDefault())
        )
    }
}
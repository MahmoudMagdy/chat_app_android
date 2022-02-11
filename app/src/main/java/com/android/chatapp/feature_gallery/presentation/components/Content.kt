package com.android.chatapp.feature_gallery.presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.chatapp.feature_chat.presentation.util.UiError

@Composable
fun EmptyContent(modifier: Modifier = Modifier, text: String) {
    Box(modifier = modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = text,
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun ErrorContent(modifier: Modifier = Modifier, title: String, content: String) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(bottom = 4.dp),
            text = title,
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        Text(
            text = content,
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ErrorContent(modifier: Modifier = Modifier, @StringRes title: Int, @StringRes content: Int) {
    ErrorContent(
        modifier = modifier,
        title = stringResource(id = title),
        content = stringResource(id = content)
    )
}

@Composable
fun ErrorContent(modifier: Modifier = Modifier, error: UiError) {
    ErrorContent(
        modifier = modifier,
        title = stringResource(id = error.title),
        content = stringResource(id = error.content)
    )
}

@Composable
fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

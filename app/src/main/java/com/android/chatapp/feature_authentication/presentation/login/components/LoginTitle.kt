package com.android.chatapp.feature_authentication.presentation.login.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@Composable
fun ColumnScope.LoginTitle(
    modifier: Modifier = Modifier,
    @StringRes title: Int
) {
    Text(
        text = stringResource(id = title),
        modifier = modifier.align(Alignment.Start),
        style = MaterialTheme.typography.h6
    )
}
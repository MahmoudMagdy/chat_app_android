package com.android.chatapp.feature_chat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.chatapp.R
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.ui.theme.ChatAppTheme

@Composable
fun SearchContent(
    input: String,
    onValueChanged: (String) -> Unit,
    onSearchClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.medium,
                top = MaterialTheme.spacing.extraSmall,
                end = MaterialTheme.spacing.medium,
                bottom = 12.dp
            )
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            modifier = Modifier.weight(1.0f),
            value = input,
            onValueChange = onValueChanged,
            placeholder = {
                Text(text = stringResource(id = R.string.chat_list_users_search_hint))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = stringResource(id = R.string.chat_list_users_search_hint)
                )
            },
            maxLines = 1
        )
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
        IconButton(onClick = onSearchClicked) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = stringResource(id = R.string.chat_list_users_search_hint)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchContentPreview() {
    ChatAppTheme {
        Surface(modifier = Modifier.background(MaterialTheme.colors.primary)) {
            SearchContent(
                input = "",
                onValueChanged = {},
                onSearchClicked = {}
            )
        }
    }
}

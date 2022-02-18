package com.android.chatapp.feature_chat.presentation.chat_list

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.chatapp.R
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.feature_authentication.presentation.authActivity
import com.android.chatapp.feature_chat.domain.model.Chat
import com.android.chatapp.feature_chat.presentation.chat_list.components.ChatItem
import com.android.chatapp.feature_chat.presentation.chat_list.components.ChatItemEvent
import com.android.chatapp.feature_chat.presentation.components.AppBarContent
import com.android.chatapp.feature_chat.presentation.components.SearchContent
import com.android.chatapp.feature_chat.presentation.routeToChat
import com.android.chatapp.feature_chat.presentation.util.Screen
import com.android.chatapp.feature_chat.presentation.util.UiState
import com.android.chatapp.feature_gallery.presentation.components.EmptyContent
import com.android.chatapp.feature_gallery.presentation.components.ErrorContent
import com.android.chatapp.feature_gallery.presentation.components.LoadingContent
import kotlinx.coroutines.flow.collect


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatListScreen(
    launch: (Context.() -> Unit) -> Unit,
    navigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                ChatListUiEvent.Logout -> launch { authActivity }
                is ChatListUiEvent.NavigateToChat -> {
                    navigate(routeToChat(cid = event.chat.id))
                }
                is ChatListUiEvent.NavigateToUserProfile -> {
                    //navigate()
                }
                is ChatListUiEvent.NavigateToSearchProfiles -> navigate("${Screen.PROFILE_SEARCH.route}${event.keyword}")
            }
        }
    }
    BackdropScaffold(
        modifier = modifier,
        appBar = {
            AppBarContent(onLogoutClick = { viewModel.onEvent(ChatListEvent.Logout) })
        },
        backLayerContent = {
            SearchContent(
                input = viewModel.userSearchKeywords,
                onValueChanged = {
                    viewModel.onEvent(ChatListEvent.UserSearchValueChanged(it))
                },
                onSearchClicked = { viewModel.onEvent(ChatListEvent.UserSearchButtonClicked) }
            )
        },
        frontLayerContent = {
            val paddingModifier = Modifier.padding(all = MaterialTheme.spacing.medium)
            uiState.apply {
                when (this) {
                    UiState.Empty -> EmptyContent(
                        modifier = paddingModifier,
                        text = stringResource(id = R.string.chat_list_empty_content)
                    )
                    is UiState.Error -> if (items == null)
                        ErrorContent(modifier = paddingModifier, error = error)
                    else
                    /**
                     * In case error occurred and there is cached data show cached chats
                     * with error message
                     */
                        LoadedContent(chats = items, viewModel = viewModel)
                    is UiState.Loaded -> LoadedContent(chats = items, viewModel = viewModel)
                    UiState.Loading -> LoadingContent(modifier = paddingModifier)
                }
            }
        })
}

@Composable
fun LoadedContent(
    chats: List<Chat>,
    viewModel: ChatListViewModel
) {
    LoadedContent(
        chats = chats,
        onItemClick = { event ->
            viewModel.onEvent(ChatListEvent.ChatItemClicked(event))
        }
    )

}

//TODO: should contain error message to show if error occurred and there's cached data
@Composable
fun LoadedContent(
    modifier: Modifier = Modifier,
    chats: List<Chat>,
    onItemClick: (ChatItemEvent) -> Unit
) {
    Box {
        LazyColumn {
            item {
                Text(
                    modifier = Modifier.padding(
                        start = MaterialTheme.spacing.medium,
                        end = MaterialTheme.spacing.medium,
                        top = MaterialTheme.spacing.extraMedium,
                        bottom = MaterialTheme.spacing.extraSmall
                    ),
                    text = stringResource(id = R.string.chat_list_title),
                    style = MaterialTheme.typography.caption.copy(color = Color.DarkGray)
                )
            }
            items(chats) { item ->
                ChatItem(chat = item, onEvent = onItemClick)
                Divider()
            }
        }
    }
}


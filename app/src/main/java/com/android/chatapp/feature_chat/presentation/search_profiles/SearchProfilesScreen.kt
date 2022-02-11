package com.android.chatapp.feature_chat.presentation.search_profiles

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.chatapp.R
import com.android.chatapp.core.presentation.util.isScrolledToTheEnd
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.feature_authentication.domain.model.User
import com.android.chatapp.feature_authentication.presentation.authActivity
import com.android.chatapp.feature_chat.presentation.components.AppBarContent
import com.android.chatapp.feature_chat.presentation.components.SearchContent
import com.android.chatapp.feature_chat.presentation.navigateToChat
import com.android.chatapp.feature_chat.presentation.search_profiles.components.UserItem
import com.android.chatapp.feature_chat.presentation.search_profiles.components.UserItemEvent
import com.android.chatapp.feature_chat.presentation.util.PaginationUiState
import com.android.chatapp.feature_gallery.presentation.components.EmptyContent
import com.android.chatapp.feature_gallery.presentation.components.ErrorContent
import com.android.chatapp.feature_gallery.presentation.components.LoadingContent
import kotlinx.coroutines.flow.collect

const val KEYWORD = "keyword"

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchProfilesScreen(
    launch: (Context.() -> Unit) -> Unit,
    navigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchProfilesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userSearchKeywords by viewModel.userSearchKeywords.collectAsState()
    val listState = rememberLazyListState()
    val loadNext by remember { derivedStateOf { listState.isScrolledToTheEnd } }
    if (loadNext) viewModel.checkNextItems()
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                SearchProfilesUiEvent.Logout -> launch { authActivity }
                is SearchProfilesUiEvent.NavigateToChat -> {
                    navigate(navigateToChat(oid = event.user.id))
                }
                is SearchProfilesUiEvent.NavigateToUserProfile -> {
                    //navigate()
                }
            }
        }
    }
    BackdropScaffold(
        modifier = modifier,
        appBar = {
            AppBarContent()
        },
        backLayerContent = {
            SearchContent(
                input = userSearchKeywords,
                onValueChanged = {
                    viewModel.onEvent(SearchProfilesEvent.UserSearchValueChanged(it))
                },
                onSearchClicked = { viewModel.onEvent(SearchProfilesEvent.UserSearchButtonClicked) }
            )
        },
        frontLayerContent = {
            val paddingModifier = Modifier.padding(all = MaterialTheme.spacing.medium)
            uiState.apply {
                when (this) {
                    PaginationUiState.Empty -> EmptyContent(
                        modifier = paddingModifier,
                        text = stringResource(id = R.string.search_profiles_empty_content)
                    )
                    is PaginationUiState.Error -> {
                        if (items == null)
                            ErrorContent(modifier = paddingModifier, error = error)
                        else
                            LoadedContent(
                                items = items,
                                viewModel = viewModel,
                                listState = listState
                            )
                    }
                    is PaginationUiState.Loaded ->
                        LoadedContent(
                            items = items,
                            viewModel = viewModel,
                            listState = listState
                        )
                    PaginationUiState.Loading -> LoadingContent(modifier = paddingModifier)
                    is PaginationUiState.NextLoading ->
                        LoadedContent(
                            items = items,
                            viewModel = viewModel,
                            listState = listState
                        )
                }
            }
        })
}

@Composable
fun LoadedContent(items: List<User>, listState: LazyListState, viewModel: SearchProfilesViewModel) {
    LoadedContent(
        users = items,
        listState = listState,
        onItemClick = { event ->
            viewModel.onEvent(
                SearchProfilesEvent.UserItemClicked(event)
            )
        }
    )
}

@Composable
fun LoadedContent(
    modifier: Modifier = Modifier,
    users: List<User>,
    listState: LazyListState,
    onItemClick: (UserItemEvent) -> Unit
) {
    LazyColumn(state = listState) {
        item {
            Text(
                modifier = Modifier.padding(
                    start = MaterialTheme.spacing.medium,
                    end = MaterialTheme.spacing.medium,
                    top = MaterialTheme.spacing.extraMedium,
                    bottom = MaterialTheme.spacing.extraSmall
                ),
                text = stringResource(id = R.string.search_profiles_title),
                style = MaterialTheme.typography.caption.copy(color = Color.DarkGray)
            )
        }
        items(users) { item ->
            UserItem(user = item, onEvent = onItemClick)
            Divider()
        }
    }
}


package com.android.chatapp.feature_chat.presentation.message_list

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.chatapp.R
import com.android.chatapp.core.presentation.util.isScrolledToTheEnd
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.feature_authentication.domain.model.Profile
import com.android.chatapp.feature_authentication.domain.model.User
import com.android.chatapp.feature_authentication.presentation.authActivity
import com.android.chatapp.feature_chat.domain.model.Message
import com.android.chatapp.feature_chat.presentation.message_list.components.MessageItem
import com.android.chatapp.feature_chat.presentation.message_list.components.MessageListAppBar
import com.android.chatapp.feature_chat.presentation.message_list.components.MessageSendBar
import com.android.chatapp.feature_chat.presentation.message_list.components.OtherMessageItem
import com.android.chatapp.feature_gallery.presentation.components.EmptyContent
import com.android.chatapp.feature_gallery.presentation.components.ErrorContent
import com.android.chatapp.feature_gallery.presentation.components.LoadingContent
import kotlinx.coroutines.flow.collect


const val USER_ID = "uid"
const val CHAT_ID = "cid"


@Composable
fun MessageListScreen(
    launch: (Context.() -> Unit) -> Unit,
    navigate: (String) -> Unit,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MessageListViewModel = hiltViewModel()
) {
    val screenState = viewModel.screenState
    val user = screenState.user
    LaunchedEffect(key1 = true) {
        viewModel.retrieveData()
        viewModel.uiEvent.collect { event ->
            when (event) {
                MessageListUiEvent.Logout -> launch { authActivity }
                is MessageListUiEvent.PopBackStack -> popBackStack()
                is MessageListUiEvent.NavigateToUserProfile -> {
                    //navigate()
                }
            }
        }
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            MessageListAppBar(
                profile = user?.profile,
                onEvent = { viewModel.onEvent(MessageListEvent.OnAppBarEvent(it)) }
            )
        }
    ) {
        Column {
            MessageListBody(
                screenState = screenState,
                user = user,
                viewModel = viewModel,
                modifier = Modifier.padding(paddingValues = it)
            )
            MessageSendBar(
                modifier = Modifier.padding(
                    vertical = MaterialTheme.spacing.small,
                    horizontal = MaterialTheme.spacing.extraMedium
                ),
                message = viewModel.message,
                onValueChange = { message ->
                    viewModel.onEvent(MessageListEvent.MessageValueChanged(message))
                },
                onSendClicked = { viewModel.onEvent(MessageListEvent.SendButtonClicked) }
            )
        }
    }
}

@Composable
fun ColumnScope.MessageListBody(
    screenState: ScreenState,
    user: User?,
    viewModel: MessageListViewModel,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val loadNext by remember { derivedStateOf { listState.isScrolledToTheEnd } }
    if (loadNext) viewModel.checkNextItems()
    val messages = screenState.messages
    val error = screenState.error
    Box(modifier = Modifier.weight(1.0f)) {
        when {
            //First Payload Loading
            screenState.isFirstLoading -> LoadingContent(modifier = modifier)
            //Next Payload Loading
            screenState.loading ->
                //TODO: show loading dots
                LoadedContent(
                    items = messages,
                    user = user,
                    viewModel = viewModel,
                    listState = listState
                )
            error != null -> {
                if (messages.isEmpty())
                    ErrorContent(modifier = modifier, error = error)
                else
                //TODO: show error on a label on the top of the chat
                    LoadedContent(
                        items = messages,
                        user = user,
                        viewModel = viewModel,
                        listState = listState
                    )
            }
            messages.isEmpty() -> EmptyContent(
                modifier = modifier,
                text = stringResource(id = R.string.msg_list_empty_content)
            )
            else ->
                LoadedContent(
                    items = messages,
                    user = user,
                    viewModel = viewModel,
                    listState = listState
                )
        }
    }
}

@Composable
fun LoadedContent(
    items: List<Message>,
    user: User?,
    listState: LazyListState,
    viewModel: MessageListViewModel
) {
    val profile = user?.profile
    if (profile != null)
        LoadedContent(
            modifier = Modifier.fillMaxSize(),
            profile = profile,
            messages = items,
            listState = listState,
            checkOwner = viewModel::checkOwner
        )
}

@Composable
fun LoadedContent(
    modifier: Modifier = Modifier,
    profile: Profile,
    messages: List<Message>,
    listState: LazyListState,
    checkOwner: (Message) -> Boolean,
) {
    LazyColumn(modifier = modifier, state = listState, reverseLayout = true) {
        item {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
        }
        itemsIndexed(messages) { index, item ->
            if (checkOwner(item))
                MessageItem(message = item)
            else
                OtherMessageItem(
                    message = item,
                    profile = profile,
                    displayImage = index == 0 || checkOwner(messages[index - 1])
                )
        }
    }
}

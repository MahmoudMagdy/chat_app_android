package com.android.chatapp.core.presentation.util

import androidx.compose.foundation.lazy.LazyListState

/**
 * [isScrolledToTheEnd] and [lastVisibleItemIndex] both are used for same purpose,
 * but i prefer [isScrolledToTheEnd]
 *
 * **Note**
 * @property lastOrNull() get the last element being displayed in the screen, and its index gives its position to the whole list
 * @property lastIndex gives last element being displayed in the screen position to the displayed items on screen not whole list
 */

val LazyListState.isScrolledToTheEnd
    get() = layoutInfo.visibleItemsInfo.lastOrNull()?.index == (layoutInfo.totalItemsCount - 1)

val LazyListState.lastVisibleItemIndex
    get() = layoutInfo.visibleItemsInfo.lastIndex + firstVisibleItemIndex
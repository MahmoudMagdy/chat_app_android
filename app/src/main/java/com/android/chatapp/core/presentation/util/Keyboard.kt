package com.android.chatapp.core.presentation.util

import androidx.compose.ui.text.input.ImeAction

val String.imeAction: ImeAction
    @Throws(InvalidImeAction::class)
    get() = when (this) {
        "None" -> ImeAction.None
        "Default" -> ImeAction.Default
        "Go" -> ImeAction.Go
        "Search" -> ImeAction.Search
        "Send" -> ImeAction.Send
        "Previous" -> ImeAction.Previous
        "Next" -> ImeAction.Next
        "Done" -> ImeAction.Done
        else -> throw InvalidImeAction()
    }


class InvalidImeAction : Exception()
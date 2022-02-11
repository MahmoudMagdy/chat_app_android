package com.android.chatapp.feature_chat.presentation.message_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.android.chatapp.core.data.util.PaginationResult
import com.android.chatapp.feature_authentication.domain.model.User
import com.android.chatapp.feature_chat.domain.model.Chat
import com.android.chatapp.feature_chat.domain.model.Message
import com.android.chatapp.feature_chat.presentation.util.UiError

class ScreenState {
    private val _messages: SnapshotStateList<Message> = mutableStateListOf()
    val messages: List<Message> get() = _messages
    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf<UiError?>(null)
        private set
    val latestMessageId: Long get() = _messages.lastOrNull()?.id ?: -1

    private var latestResponse: PaginationResult<Message>? = null
    val page: Int get() = latestResponse?.next ?: 1
    val isFirstPage get() = latestResponse == null
    val isFirstLoading get() = isFirstPage && loading

    val loadAbility get() = (latestResponse == null || latestResponse?.next != null)

    val stopLoadAbility get() = run { latestResponse = PaginationResult(null, null, listOf()) }
    val stopLoading get() = run { loading = false }
    val startLoading get() = run { loading = true }
    private val cleanError get() = run { error = null }

    var chat: Chat? = null
        set(value) {
            if (value != null) user = value.user
            field = value
        }

    var user by mutableStateOf<User?>(null)

    @Volatile
    private var lastTransaction: ListTransaction? = null
    private val lock = Any()

    private val cleanTransactions
        get() = synchronized(lock) {
            val transaction = lastTransaction
            if (transaction != null) {
                lastTransaction = null
                if (transaction is ListTransaction.Add) {
                    _messages.removeRange(transaction.start, transaction.end)
                }
            }
        }

    fun addMessage(message: Message) {
        _messages.add(0, message)
    }

    private fun addMessages(messages: List<Message>, temporary: Boolean) {
        cleanTransactions
        if (temporary) {
            val size = _messages.size
            lastTransaction = ListTransaction.Add(size, size + messages.size)
        }
        if (messages.isNotEmpty()) _messages.addAll(messages)
    }

    fun setSuccessData(response: PaginationResult<Message>) {
        stopLoading
        cleanError
        latestResponse = response
        addMessages(response.results, false)
    }

    fun setError(uiError: UiError, items: List<Message>? = null) {
        stopLoading
        error = uiError
        if (items != null) addMessages(items, true)
    }

    fun startLoading(items: List<Message>?) {
        startLoading
        if (items != null) addMessages(items, true)
    }

    val clear
        get() = run {
            _messages.clear()
            latestResponse = null
            cleanError
            loading = false
        }

    sealed class ListTransaction {
        /**
         * specified [start] (inclusive) and [end] (exclusive).
         */
        data class Add(val start: Int, val end: Int) : ListTransaction()
    }
}

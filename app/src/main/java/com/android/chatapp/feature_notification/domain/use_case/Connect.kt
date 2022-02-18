package com.android.chatapp.feature_notification.domain.use_case

import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.core.data.util.WSCloseReason
import com.android.chatapp.feature_notification.domain.model.Notification
import com.android.chatapp.feature_notification.domain.repository.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Connect @Inject constructor(private val repository: NotificationRepository) {

    suspend operator fun invoke(
        onReceive: (Notification) -> Unit,
        onClose: (WSCloseReason) -> Unit,
        onError: (Throwable) -> Unit
    ) = withContext(Dispatchers.IO) {
        repository.connect().apply {
            when (this) {
                is Resource.Success -> {
                    val closed = async(Dispatchers.IO) {
                        repository.disconnect(onClose)
                    }
                    launch(Dispatchers.IO) {
                        repository.receive().collect(onReceive)
                    }
                    closed.await()
                }
                is Resource.Error -> onError(throwable)
                is Resource.Loading,
                is Resource.Failure -> Unit
            }
        }

    }
}
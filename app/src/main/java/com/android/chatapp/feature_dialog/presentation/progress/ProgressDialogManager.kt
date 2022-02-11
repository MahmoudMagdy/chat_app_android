package com.android.chatapp.feature_dialog.presentation.progress

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.android.chatapp.feature_dialog.presentation.util.DialogManager
import kotlinx.coroutines.Job

class ProgressDialogManager(onEvent: ((event: ProgressDialogEvent, job: Job?) -> Unit)? = null) :
    DialogManager {
    var state by mutableStateOf<ProgressDialogState?>(null)
        private set

    /**
     * @param job when it's canceled not means that the process is actually canceled,
     * Coroutine [Job] must itself check if "job" cancelled or no while processing rest of the "job"
     */
    val onEvent = onEvent ?: { event, job ->
        when (event) {
            ProgressDialogEvent.ACTION_CLICKED -> {
                job?.cancel()
                close
            }
            ProgressDialogEvent.DISMISS_REQUESTED -> Unit
        }
    }
    val opened get() = state != null
    val close get() = run { if (state != null) state = null }

    override fun show(@StringRes title: Int, @StringRes content: Int) {
        state = ProgressDialogState(title, content)
    }
}

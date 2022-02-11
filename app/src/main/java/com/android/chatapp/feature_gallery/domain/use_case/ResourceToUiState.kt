package com.android.chatapp.feature_gallery.domain.use_case

import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_gallery.presentation.util.Gallery
import com.android.chatapp.feature_gallery.presentation.util.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ResourceToUiState @Inject constructor() {
    operator fun <T> invoke(
        resFlow: Flow<Resource<List<T>, Nothing>>,
        gallery: Gallery
    ): Flow<UiState<T>> =
        resFlow.map { resource ->
            resource.run {
                when (this) {
                    is Resource.Error -> UiState.Error(exception = throwable)
                    is Resource.Failure -> UiState.Error(exception = Exception())
                    is Resource.Loading -> UiState.Loading
                    is Resource.Success -> if (data.isEmpty()) UiState.Empty
                    else UiState.Loaded(data, gallery)
                }
            }
        }
}


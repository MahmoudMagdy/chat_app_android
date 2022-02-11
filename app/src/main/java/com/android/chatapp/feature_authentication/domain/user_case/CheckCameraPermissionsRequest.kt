package com.android.chatapp.feature_authentication.domain.user_case

import android.content.Context
import com.android.chatapp.core.domain.util.PermissionRequestState
import com.android.chatapp.core.domain.util.PermissionResult
import com.android.chatapp.core.domain.util.permissionsRequestState
import javax.inject.Inject

class CheckCameraPermissionsRequest @Inject constructor() {
    operator fun invoke(
        permissions: List<PermissionResult>,
        context: Context
    ): PermissionRequestState =
        context.permissionsRequestState(permissions)
}


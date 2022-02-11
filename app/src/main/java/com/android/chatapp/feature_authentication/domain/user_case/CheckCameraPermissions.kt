package com.android.chatapp.feature_authentication.domain.user_case

import android.Manifest
import android.content.Context
import com.android.chatapp.core.domain.util.PermissionsState
import com.android.chatapp.core.domain.util.permissionsState
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CheckCameraPermissions @Inject constructor(@ApplicationContext val context: Context) {
    operator fun invoke(): PermissionsState =
        context.permissionsState(
            listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        )
}


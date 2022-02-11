package com.android.chatapp

import com.android.chatapp.core.domain.util.PermissionResult
import com.android.chatapp.core.domain.util.granted
import org.junit.Test

class PermissionsUtilTest {
    @Test
    fun allPermissionsGranted() {
        val granted = listOf(
            PermissionResult("", true),
            PermissionResult("", true),
            PermissionResult("", true),
            PermissionResult("", true),
            PermissionResult("", true),
        ).granted
        assert(granted)
    }

    @Test
    fun notAllPermissionsGranted() {
        val granted = listOf(
            PermissionResult("", true),
            PermissionResult("", true),
            PermissionResult("", false),
            PermissionResult("", true),
            PermissionResult("", true),
        ).granted
        assert(!granted)
    }
}
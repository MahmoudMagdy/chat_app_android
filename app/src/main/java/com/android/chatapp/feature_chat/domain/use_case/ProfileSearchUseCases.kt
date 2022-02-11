package com.android.chatapp.feature_chat.domain.use_case

import com.android.chatapp.feature_authentication.domain.user_case.Logout
import javax.inject.Inject

data class ProfileSearchUseCases @Inject constructor(
    val searchProfiles: SearchProfiles,
    val logout: Logout
)
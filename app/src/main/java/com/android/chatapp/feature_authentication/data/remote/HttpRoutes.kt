package com.android.chatapp.feature_authentication.data.remote

import com.android.chatapp.core.data.remote.HttpRoutes.BASE_URL

object HttpRoutes {
    private const val AUTH_URL = "$BASE_URL/auth"
    const val LOGIN_URL = "$AUTH_URL/login/"
    const val REGISTER_URL = "$AUTH_URL/register/"
    const val RESET_PASSWORD_URL = "$AUTH_URL/reset-password-request/"
    const val REFRESH_TOKENS_URL = "$AUTH_URL/tokens/refresh/"
    const val PROFILE_UPLOAD_URL = "$AUTH_URL/profile/upload-url/"
    const val CREATE_PROFILE_URL = "$AUTH_URL/profile/create/"
    const val CURRENT_PROFILE_URL = "$AUTH_URL/profile/current/"
    private const val USER_DETAIL_URL = "$AUTH_URL/user/detail/"

    fun getSearchUrl(keyword: String, page: Int, limit: Int) =
        "$AUTH_URL/user/list/?search=$keyword&page=$page&page_size=$limit"

    fun getUserUrl(id: Long) =
        "$USER_DETAIL_URL$id/"
}
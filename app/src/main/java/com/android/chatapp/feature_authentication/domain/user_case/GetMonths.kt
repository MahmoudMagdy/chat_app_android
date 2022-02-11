package com.android.chatapp.feature_authentication.domain.user_case

import java.util.*
import javax.inject.Inject


class GetMonths @Inject constructor() {

    operator fun invoke(): List<String> {
        val calendar = Calendar.getInstance()
        val locale = Locale.getDefault()
        return List(12) { month ->
            calendar.apply {
                set(Calendar.MONTH, month)
            }.getDisplayName(Calendar.MONTH, Calendar.LONG, locale)
        }.filterNotNull()
    }
}
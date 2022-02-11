package com.android.chatapp.feature_authentication.domain.user_case

import com.android.chatapp.feature_authentication.presentation.user_info.components.ITEM_NOT_EXISTS
import java.util.*
import javax.inject.Inject


class GetDays @Inject constructor() {

    operator fun invoke(month: Int, year: String): List<String> {
        val lastDay = Calendar.getInstance().apply {
            set(year.toIntOrNull() ?: 1, if (month != ITEM_NOT_EXISTS) month else 1, 1)
        }.getActualMaximum(Calendar.DAY_OF_MONTH)
        return List(lastDay) { (it + 1).toString() }
    }
}
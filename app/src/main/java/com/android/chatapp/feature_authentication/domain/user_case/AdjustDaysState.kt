package com.android.chatapp.feature_authentication.domain.user_case

import com.android.chatapp.feature_authentication.presentation.user_info.components.DropDownMenuInputState
import javax.inject.Inject


class AdjustDaysState @Inject constructor() {

    operator fun invoke(
        month: Int,
        year: String,
        daysState: DropDownMenuInputState,
        getDays: GetDays
    ) {
        val days = getDays(month, year)
        val selectedDay = daysState.selectedOption.value
        if (days.size != daysState.options.value.size)
            daysState.setOptions(days)
        if (selectedDay.isNotBlank() && selectedDay !in days)
            daysState.removeSelection()
    }
}
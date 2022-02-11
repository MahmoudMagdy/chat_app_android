package com.android.chatapp.feature_authentication.domain.user_case

import java.util.*
import javax.inject.Inject


class GetYears @Inject constructor() {

    operator fun invoke(): List<String> {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        return List(year - MIN_YEAR) { (year - it).toString() }
    }

    companion object {
        private const val MIN_YEAR = 1897
    }
}
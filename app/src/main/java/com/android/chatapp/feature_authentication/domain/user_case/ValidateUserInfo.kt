package com.android.chatapp.feature_authentication.domain.user_case

import android.content.Context
import android.telephony.TelephonyManager
import com.android.chatapp.R
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_authentication.data.remote.dto.CreateProfileRequest
import com.android.chatapp.feature_authentication.domain.model.Gender
import com.android.chatapp.feature_authentication.domain.util.LoginFieldError
import com.android.chatapp.feature_authentication.presentation.user_info.components.ITEM_NOT_EXISTS
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.datetime.LocalDate
import java.util.*
import javax.inject.Inject

class ValidateUserInfo @Inject constructor(@ApplicationContext val context: Context) {

    operator fun invoke(
        fName: String, lName: String,
        day: String, month: Int, year: String,
        gender: Gender,
    ): Resource<CreateProfileRequest, LoginFieldError> {
        val cleanFName = fName.trim()
        val cleanLName = lName.trim()
        mutableListOf<LoginFieldError>().apply {
            if (cleanFName.isBlank())
                add(LoginFieldError.FirstName(R.string.user_info_f_name_empty))
            if (cleanLName.isBlank())
                add(LoginFieldError.LastName(R.string.user_info_l_name_empty))
            if (day.isEmpty() || month == ITEM_NOT_EXISTS || year.isEmpty())
                add(LoginFieldError.Date(R.string.user_info_provide_dob_content))
            if (isNotEmpty())
                return@invoke Resource.Failure(this)
        }
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val countryCode = tm.networkCountryIso?.uppercase(Locale.US)
        val locale = Locale.getDefault()
        return Resource.Success(
            CreateProfileRequest(
                firstName = cleanFName,
                lastName = cleanLName,
                countryCode = countryCode ?: locale.country,
                deviceLanguage = locale.language,
                gender = gender,
                birthdate = LocalDate(year.toInt(), month + 1, day.toInt()),
                image = null
            )
        )
    }
}
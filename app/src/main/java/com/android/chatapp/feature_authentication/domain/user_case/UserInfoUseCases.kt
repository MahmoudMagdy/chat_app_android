package com.android.chatapp.feature_authentication.domain.user_case

import com.android.chatapp.core.domain.user_case.CheckConnection
import javax.inject.Inject

data class UserInfoUseCases @Inject constructor(
    val checkCameraPermissions: CheckCameraPermissions,
    val checkCameraPermissionsRequest: CheckCameraPermissionsRequest,
    val saveProfilePhoto: SaveProfilePhoto,
    val validateUserInfo: ValidateUserInfo,
    val profileUploadUrl: GenerateProfileUploadUrl,
    val attachPhotoToRequest: AttachPhotoToRequest,
    val uploadProfilePhoto: UploadProfilePhoto,
    val createProfile: CreateProfile,
    val checkConnection: CheckConnection,
    val getDays: GetDays,
    val getMonths: GetMonths,
    val getYears: GetYears,
    val adjustDaysState: AdjustDaysState,
)
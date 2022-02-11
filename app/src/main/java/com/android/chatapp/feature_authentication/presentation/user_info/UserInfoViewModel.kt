package com.android.chatapp.feature_authentication.presentation.user_info

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.chatapp.R
import com.android.chatapp.core.data.util.ApiException
import com.android.chatapp.core.data.util.NetworkResponseException
import com.android.chatapp.core.domain.util.PermissionRequestState
import com.android.chatapp.core.domain.util.PermissionResult
import com.android.chatapp.core.domain.util.PermissionsState
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.feature_authentication.data.remote.dto.CreateProfileRequest
import com.android.chatapp.feature_authentication.data.remote.dto.ProfileResponse
import com.android.chatapp.feature_authentication.data.remote.dto.ProfileUploadUrlResponse
import com.android.chatapp.feature_authentication.data.util.AuthApiExceptions
import com.android.chatapp.feature_authentication.domain.model.Gender
import com.android.chatapp.feature_authentication.domain.model.ProfileMediaType
import com.android.chatapp.feature_authentication.domain.user_case.UserInfoUseCases
import com.android.chatapp.feature_authentication.domain.util.LoginFieldError
import com.android.chatapp.feature_authentication.presentation.components.EditableUserInputState
import com.android.chatapp.feature_authentication.presentation.user_info.components.DropDownMenuInputState
import com.android.chatapp.feature_authentication.presentation.user_info.components.IconChipState
import com.android.chatapp.feature_authentication.presentation.user_info.components.ProfilePhotoSource
import com.android.chatapp.feature_dialog.domain.model.attach
import com.android.chatapp.feature_dialog.presentation.image_selector.ImageSelectorDialogManager
import com.android.chatapp.feature_dialog.presentation.image_selector.ImageSelectorEvent
import com.android.chatapp.feature_dialog.presentation.message.MessageDialogEvent
import com.android.chatapp.feature_dialog.presentation.message.MessageDialogManager
import com.android.chatapp.feature_dialog.presentation.progress.ProgressDialogManager
import com.android.chatapp.feature_dialog.presentation.util.to
import com.android.chatapp.feature_gallery.domain.model.LocalMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(val cases: UserInfoUseCases) : ViewModel() {
    val firstNameState = EditableUserInputState(
        hint = R.string.user_info_first_name,
        initialAction = ImeAction.Next
    )
    val lastNameState = EditableUserInputState(
        hint = R.string.user_info_last_name,
        initialAction = ImeAction.Next
    )

    val maleState = IconChipState(
        text = R.string.user_info_gender_male_title,
        initialSelected = true
    )
    val femaleState = IconChipState(
        text = R.string.user_info_gender_female_title,
        initialSelected = false
    )

    val monthsState = DropDownMenuInputState(
        hint = R.string.user_info_month_title,
    )
    val yearsState = DropDownMenuInputState(
        hint = R.string.user_info_year_title,
    )
    val daysState = DropDownMenuInputState(
        hint = R.string.user_info_day_title,
    )

    private val defProfilePhoto = ProfilePhotoSource.Resource(R.drawable.def_profile_pic)
    var profilePhotoSrc: ProfilePhotoSource by mutableStateOf(defProfilePhoto)
        private set

    val messageDialog = MessageDialogManager(::onMessageDialogEvent)

    val progressDialog = ProgressDialogManager()

    val imageSelectorDialog = ImageSelectorDialogManager(onEvent = ::onImageSelectorEvent)

    private var profileJob: Job? = null

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent get() = _uiEvent.receiveAsFlow()

    init {
        initBirthData()
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    fun onEvent(event: UserInfoEvent) {
        event.apply {
            when (this) {
                UserInfoEvent.LoginButtonClicked -> onLoginClicked()
                UserInfoEvent.ChoosePhotoClicked -> imageSelectorDialog.show(R.string.img_selector_profile_title)
                UserInfoEvent.PrivacyPolicyClicked -> sendUiEvent(UiEvent.LaunchPrivacyPolicy)
                UserInfoEvent.TermsClicked -> sendUiEvent(UiEvent.LaunchTerms)
                is UserInfoEvent.GenderChipClicked -> onGenderClicked(gender)
                is UserInfoEvent.OnMessageDialogEvent -> messageDialog.onEvent(this.event)
                is UserInfoEvent.OnProgressDialogEvent ->
                    progressDialog.onEvent(this.event, profileJob)
                is UserInfoEvent.ImageItemChosen -> onImageChosen(image)
                is UserInfoEvent.OnImageSelectorDialogEvent -> imageSelectorDialog.onEvent(this.event)
                is UserInfoEvent.ImageTaken -> onImageTaken(bitmap)
                is UserInfoEvent.OnCameraPermissionsResult ->
                    onCameraPermissionsRequestResult(result, context)
            }
        }
    }

    private fun onImageSelectorEvent(imageSelectorEvent: ImageSelectorEvent) {
        imageSelectorDialog.close
        when (imageSelectorEvent) {
            ImageSelectorEvent.CAMERA_CLICKED ->
                cases.checkCameraPermissions().onCameraPermissionsResult()
            ImageSelectorEvent.GALLERY_CLICKED -> sendUiEvent(UiEvent.LaunchImageGallery())
            ImageSelectorEvent.REMOVE_CLICKED -> profilePhotoSrc = defProfilePhoto
            ImageSelectorEvent.DISMISS_REQUESTED,
            ImageSelectorEvent.ACTION_CLICKED -> Unit
        }
    }

    private fun PermissionsState.onCameraPermissionsResult() {
        when (this) {
            PermissionsState.Accepted -> sendUiEvent(UiEvent.LaunchCamera)
            is PermissionsState.Required -> sendUiEvent(UiEvent.RequestCameraPermissions(permissions))
        }
    }

    /**
     * **Warning:**
     * [PermissionRequestState.PermanentDenied] May emit due to user clicked outside
     * the request permission dialog until user click deny at least once.
     **/
    private fun onCameraPermissionsRequestResult(result: List<PermissionResult>, context: Context) {
        when (cases.checkCameraPermissionsRequest(result, context)) {
            PermissionRequestState.Accepted -> sendUiEvent(UiEvent.LaunchCamera)
            is PermissionRequestState.ShouldShowRational ->
                messageDialog.show(
                    R.string.gen_insufficient_permissions_title,
                    R.string.gen_camera_rejected_msg,
                    R.string.gen_request_permission_btn, ACTION_REQUEST_PERMISSION
                )
            PermissionRequestState.PermanentDenied -> messageDialog.show(
                R.string.gen_insufficient_permissions_title,
                R.string.gen_camera_do_not_show_again_msg,
                R.string.gen_open_settings_btn, ACTION_OPEN_SETTINGS
            )
        }
    }

    private fun onMessageDialogEvent(event: MessageDialogEvent) {
        when (event) {
            MessageDialogEvent.DismissRequested -> messageDialog.close
            is MessageDialogEvent.ActionClicked -> {
                when (event.action) {
                    ACTION_REQUEST_PERMISSION -> cases.checkCameraPermissions()
                        .onCameraPermissionsResult()
                    ACTION_OPEN_SETTINGS -> sendUiEvent(UiEvent.LaunchAppSettings)
                }
                messageDialog.close
            }
        }
    }

    private fun onImageTaken(bitmap: Bitmap?) {
        if (bitmap != null) {
            viewModelScope.launch {
                cases.saveProfilePhoto(bitmap).collect { result ->
                    profilePhotoSrc = when (result) {
                        is Resource.Error,
                        is Resource.Failure -> {
                            messageDialog.generalError
                            defProfilePhoto
                        }
                        is Resource.Loading -> ProfilePhotoSource.Loading(R.drawable.def_profile_pic)
                        is Resource.Success -> ProfilePhotoSource.Media(result.data)
                    }
                }
            }
        }
    }

    private fun onImageChosen(image: LocalMedia?) {
        if (image != null) {
            if (image.sizeMB <= MAXIMUM_IMAGE_SIZE_MB)
                profilePhotoSrc = ProfilePhotoSource.Media(image)
            else
                R.string.gen_img_size_exceeded_limit_title attach R.string.gen_img_size_exceeded_limit_msg to messageDialog
        }
    }


    /** ***Notes:***
     *  [Calendar].getInstance().set(year, month, date)
     *  year – the value used to set the YEAR calendar field.
     *  month – the value used to set the MONTH calendar field. Month value is 0-based. e.g., 0 for January.
     *  date – the value used to set the DAY_OF_MONTH calendar field.
     **/
    private fun initBirthData() {
        viewModelScope.launch {
            monthsState.setOptions(cases.getMonths())
            yearsState.setOptions(cases.getYears())
            daysState.setOptions(
                cases.getDays(
                    monthsState.selectedOptionPosition,
                    yearsState.selectedOption.value
                )
            )
        }
        monthsState.onSelectionChanged(viewModelScope) { _, position ->
            cases.adjustDaysState(
                position,
                yearsState.selectedOption.value,
                daysState,
                cases.getDays
            )
        }
        yearsState.onSelectionChanged(viewModelScope) { year, _ ->
            cases.adjustDaysState(
                monthsState.selectedOptionPosition,
                year,
                daysState,
                cases.getDays
            )
        }
    }

    private fun onGenderClicked(gender: Gender) {
        val isMale = gender == Gender.MALE
        maleState.setSelection(isMale)
        femaleState.setSelection(!isMale)
    }

    private fun onLoginClicked() {
        if (cases.checkConnection())
            cases.validateUserInfo(
                fName = firstNameState.text,
                lName = lastNameState.text,
                day = daysState.selectedOption.value,
                month = monthsState.selectedOptionPosition,
                year = yearsState.selectedOption.value,
                gender = if (maleState.selected) Gender.MALE else Gender.FEMALE
            ).onUserInfoValidated()
        else messageDialog.networkError
    }

    private fun Resource<CreateProfileRequest, LoginFieldError>.onUserInfoValidated() {
        when (this) {
            is Resource.Success -> createProfile(data, profilePhotoSrc)
            is Resource.Failure -> {
                errors.forEach { error ->
                    when (error) {
                        is LoginFieldError.FirstName -> firstNameState.setErrorValue(error.msg)
                        is LoginFieldError.LastName -> lastNameState.setErrorValue(error.msg)
                        is LoginFieldError.Date -> R.string.gen_incomplete_data_title attach error.msg to messageDialog
                        else -> Unit
                    }
                }
            }
            else -> Unit
        }
    }

    private fun createProfile(data: CreateProfileRequest, profilePhotoSrc: ProfilePhotoSource) {
        profilePhotoSrc.apply {
            when (this) {
                is ProfilePhotoSource.Media -> profileJob = viewModelScope.launch(Dispatchers.IO) {
                    cases.profileUploadUrl(
                        ProfileMediaType.IMAGE,
                        media.extension,
                        media.size
                    ).collect { result -> onGenerateUrlResult(result, data, media) }
                }
                is ProfilePhotoSource.Resource -> setProfileData(data)
                is ProfilePhotoSource.Loading ->
                    R.string.gen_camera_not_saved_yet_title attach R.string.gen_camera_not_saved_yet_msg to messageDialog
            }
        }
    }

    private fun onGenerateUrlResult(
        result: Resource<ProfileUploadUrlResponse, ApiException>,
        profileRequest: CreateProfileRequest,
        image: LocalMedia
    ) {
        result.apply {
            when (this) {
                is Resource.Loading -> showLoadingUi()
                is Resource.Success -> onGenerateUrlSuccess(data, profileRequest, image)
                is Resource.Failure -> {
                    progressDialog.close
                    onGenerateUrlFailure(errors)
                }
                is Resource.Error -> {
                    progressDialog.close
                    if (throwable is NetworkResponseException) messageDialog.networkError
                    else messageDialog.generalError
                }
            }
        }
    }

    private fun onGenerateUrlSuccess(
        urlResponse: ProfileUploadUrlResponse,
        oldProfileRequest: CreateProfileRequest,
        image: LocalMedia
    ) {
        val profileRequest = cases.attachPhotoToRequest(urlResponse, oldProfileRequest, image)
        profileJob = viewModelScope.launch(Dispatchers.IO) {
            cases.uploadProfilePhoto(urlResponse, image)
                .collect { onUploadResult(it, profileRequest) }
        }
    }

    private fun onUploadResult(
        result: Resource<HttpResponse, ApiException>,
        profileRequest: CreateProfileRequest
    ) {
        result.apply {
            when (this) {
                is Resource.Success -> setProfileData(profileRequest)
                is Resource.Failure -> {
                    progressDialog.close
                    messageDialog.generalError
                }
                is Resource.Error -> {
                    Log.e("dsa",throwable.message,throwable)
                    progressDialog.close
                    if (throwable is NetworkResponseException) messageDialog.networkError
                    else messageDialog.generalError
                }
                is Resource.Loading -> showLoadingUi()
            }
        }

    }


    private fun onGenerateUrlFailure(errors: List<ApiException>) {
        errors.forEach { error ->
            when (error) {
                AuthApiExceptions.dataInvalid -> messageDialog.generalError
                AuthApiExceptions.notAuthenticatedToken, AuthApiExceptions.notAuthenticated ->
                    sendUiEvent(UiEvent.NotAuthenticated)
                else -> messageDialog.generalError
            }
        }
    }

    private fun setProfileData(data: CreateProfileRequest) {
        profileJob = viewModelScope.launch(Dispatchers.IO) {
            cases.createProfile(data).collect(::onCreateResult)
        }
    }

    private fun onCreateResult(result: Resource<ProfileResponse, ApiException>) {
        result.apply {
            if (this !is Resource.Loading)
                progressDialog.close
            when (this) {
                is Resource.Loading -> showLoadingUi()
                is Resource.Success -> onCreateSuccess()
                is Resource.Failure -> onCreateFailure(errors)
                is Resource.Error ->
                    if (throwable is NetworkResponseException) messageDialog.networkError
                    else messageDialog.generalError

            }
        }
    }

    private fun onCreateSuccess() {
        sendUiEvent(UiEvent.LoginCompleted)
    }

    private fun onCreateFailure(errors: List<ApiException>) {
        errors.forEach { error ->
            when (error) {
                AuthApiExceptions.profileExists ->
                    sendUiEvent(UiEvent.ProfileAlreadyExists)
                AuthApiExceptions.notAuthenticatedToken, AuthApiExceptions.notAuthenticated ->
                    sendUiEvent(UiEvent.NotAuthenticated)
                else -> messageDialog.generalError
            }
        }
    }

    private fun showLoadingUi() {
        if (!progressDialog.opened) R.string.user_info_progress_title attach R.string.login_scr_loading_data to progressDialog
    }

    companion object {
        private const val MAXIMUM_IMAGE_SIZE_MB = 2
        private const val ACTION_REQUEST_PERMISSION = 12
        private const val ACTION_OPEN_SETTINGS = 9
    }
}

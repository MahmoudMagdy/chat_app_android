package com.android.chatapp.feature_authentication.presentation.user_info

import android.content.Context
import android.graphics.Bitmap
import com.android.chatapp.core.domain.util.PermissionResult
import com.android.chatapp.feature_authentication.domain.model.Gender
import com.android.chatapp.feature_dialog.presentation.image_selector.ImageSelectorEvent
import com.android.chatapp.feature_dialog.presentation.message.MessageDialogEvent
import com.android.chatapp.feature_dialog.presentation.progress.ProgressDialogEvent
import com.android.chatapp.feature_gallery.domain.model.LocalMedia

sealed class UserInfoEvent {
    data class OnMessageDialogEvent(val event: MessageDialogEvent) : UserInfoEvent()
    data class OnImageSelectorDialogEvent(val event: ImageSelectorEvent) : UserInfoEvent()

    data class OnProgressDialogEvent(val event: ProgressDialogEvent) : UserInfoEvent()
    data class GenderChipClicked(val gender: Gender) : UserInfoEvent()
    data class ImageItemChosen(val image: LocalMedia?) : UserInfoEvent()
    data class ImageTaken(val bitmap: Bitmap?) : UserInfoEvent()
    data class OnCameraPermissionsResult(
        val result: List<PermissionResult>,
        val context: Context
    ) : UserInfoEvent()
    object ChoosePhotoClicked : UserInfoEvent()
    object LoginButtonClicked : UserInfoEvent()
    object PrivacyPolicyClicked : UserInfoEvent()
    object TermsClicked : UserInfoEvent()
}

package com.android.chatapp.feature_authentication.presentation.user_info

import android.content.Context
import android.content.res.Configuration
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.chatapp.R
import com.android.chatapp.core.domain.util.launchMobileSettings
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.feature_agreement.presentation.privacyPolicy
import com.android.chatapp.feature_agreement.presentation.terms
import com.android.chatapp.feature_authentication.domain.model.Gender
import com.android.chatapp.feature_authentication.presentation.authActivity
import com.android.chatapp.feature_authentication.presentation.components.EditableUserInput
import com.android.chatapp.feature_authentication.presentation.components.EditableUserInputState
import com.android.chatapp.feature_authentication.presentation.user_info.components.*
import com.android.chatapp.feature_chat.presentation.chatActivity
import com.android.chatapp.feature_dialog.presentation.image_selector.ImageSelectorDialog
import com.android.chatapp.feature_dialog.presentation.message.MessageDialog
import com.android.chatapp.feature_dialog.presentation.progress.ProgressDialog
import com.android.chatapp.ui.theme.ChatAppTheme
import kotlinx.coroutines.flow.collect
import java.util.*

@Composable
fun UserInfoScreen(
    modifier: Modifier = Modifier,
    launchActivity: (Context.() -> Unit) -> Unit,
    viewModel: UserInfoViewModel = hiltViewModel()
) {
    val messageDialog = viewModel.messageDialog.state
    val progressDialog = viewModel.progressDialog.state
    val imageSelectorDialog = viewModel.imageSelectorDialog.state
    val gallery = rememberGalleryLauncher(onEvent = viewModel::onEvent)
    val camera = rememberCameraLauncher(onEvent = viewModel::onEvent)
    val context = LocalContext.current
    val cameraPermissions = rememberCameraPermissionsLauncher(onEvent = {
        viewModel.onEvent(UserInfoEvent.OnCameraPermissionsResult(it, context))
    })
    LaunchedEffect(key1 = EFFECT_KEY) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                UiEvent.PopBackStack -> TODO()
                UiEvent.LoginCompleted -> launchActivity { chatActivity }
                UiEvent.NotAuthenticated,
                UiEvent.ProfileAlreadyExists -> launchActivity { authActivity }
                UiEvent.LaunchPrivacyPolicy -> launchActivity { privacyPolicy }
                UiEvent.LaunchTerms -> launchActivity { terms }
                is UiEvent.LaunchImageGallery -> gallery.launch(event.settings)
                is UiEvent.LaunchCamera -> camera.launch()
                is UiEvent.RequestCameraPermissions -> cameraPermissions.launch(event.permissions.toTypedArray())
                UiEvent.LaunchAppSettings -> launchActivity { launchMobileSettings() }
            }
        }
    }
    Column(modifier = modifier) {
        Column(modifier = Modifier.weight(1.0f)) {
            ProfilePhotoInput(
                modifier = Modifier.align(CenterHorizontally),
                src = viewModel.profilePhotoSrc,
                onClick = { viewModel.onEvent(UserInfoEvent.ChoosePhotoClicked) }
            )
            NameInput(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                firstName = viewModel.firstNameState,
                lastName = viewModel.lastNameState
            )
            BirthdateInput(
                modifier = Modifier.padding(top = MaterialTheme.spacing.extraMedium),
                day = viewModel.daysState,
                month = viewModel.monthsState,
                year = viewModel.yearsState
            )
            GenderInput(
                modifier = Modifier.padding(top = MaterialTheme.spacing.medium),
                male = viewModel.maleState,
                female = viewModel.femaleState,
                onGenderClicked = { gender ->
                    viewModel.onEvent(UserInfoEvent.GenderChipClicked(gender))
                }
            )
            UserAgreement(
                modifier = Modifier.padding(top = MaterialTheme.spacing.extraMedium),
                onEvent = { event -> viewModel.onEvent(event) }
            )
        }
        Button(modifier = Modifier
            .padding(vertical = MaterialTheme.spacing.small)
            .align(Alignment.End),
            onClick = { viewModel.onEvent(UserInfoEvent.LoginButtonClicked) }) {
            Text(text = stringResource(id = R.string.user_info_login_btn_text).uppercase(Locale.getDefault()))
        }
    }
    AnimatedVisibility(visible = messageDialog != null) {
        messageDialog?.apply {
            MessageDialog(state = this, onEvent = {
                viewModel.onEvent(UserInfoEvent.OnMessageDialogEvent(it))
            })
        }
    }

    AnimatedVisibility(visible = imageSelectorDialog != null) {
        imageSelectorDialog?.apply {
            ImageSelectorDialog(state = this, onEvent = {
                viewModel.onEvent(UserInfoEvent.OnImageSelectorDialogEvent(it))
            })
        }
    }

    AnimatedVisibility(visible = progressDialog != null) {
        progressDialog?.apply {
            ProgressDialog(state = this, onEvent = {
                viewModel.onEvent(UserInfoEvent.OnProgressDialogEvent(it))
            })
        }
    }
}

@Composable
fun NameInput(
    modifier: Modifier = Modifier,
    firstName: EditableUserInputState,
    lastName: EditableUserInputState
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top,
    ) {
        EditableUserInput(
            state = firstName,
            modifier = Modifier
                .weight(0.5f)
                .padding(end = MaterialTheme.spacing.small)
        )
        EditableUserInput(
            state = lastName,
            modifier = Modifier.weight(0.5f)
        )
    }

}

@Composable
fun BirthdateInput(
    modifier: Modifier = Modifier,
    day: DropDownMenuInputState,
    month: DropDownMenuInputState,
    year: DropDownMenuInputState
) {
    Row(modifier = modifier) {
        DropDownMenuInput(
            modifier = Modifier.weight(0.3f),
            state = day
        )
        DropDownMenuInput(
            modifier = Modifier
                .padding(horizontal = MaterialTheme.spacing.extraSmall)
                .weight(0.5f),
            state = month
        )
        DropDownMenuInput(
            modifier = Modifier.weight(0.35f),
            state = year
        )
    }
}

@Composable
fun GenderInput(
    modifier: Modifier = Modifier,
    male: IconChipState,
    female: IconChipState,
    onGenderClicked: (Gender) -> Unit
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.extraSmall),
            text = stringResource(id = R.string.user_info_gender_title),
            style = MaterialTheme.typography.subtitle2,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
        Row {
            IconChip(
                state = male,
                onClick = { onGenderClicked(Gender.MALE) }) {
                Image(
                    painter = painterResource(id = R.drawable.user_info_gender_male),
                    contentDescription = stringResource(id = R.string.user_info_gender_male_title),
                )
            }
            Spacer(modifier = Modifier.padding(MaterialTheme.spacing.extraSmall))
            IconChip(
                state = female,
                onClick = { onGenderClicked(Gender.FEMALE) }) {
                Image(
                    painter = painterResource(id = R.drawable.user_info_gender_female),
                    contentDescription = stringResource(id = R.string.user_info_gender_female_title),
                )
            }
        }
    }
}

@Composable
fun UserAgreement(
    modifier: Modifier = Modifier,
    onEvent: (UserInfoEvent) -> Unit
) {
    CompositionLocalProvider(LocalContentAlpha provides 0.6f) {
        Row(modifier = modifier) {
            Text(
                text = stringResource(id = R.string.user_info_agreement_base_content),
                style = MaterialTheme.typography.body2
            )
            ClickableText(
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.extraSmall),
                text = R.string.user_info_agreement_terms_content,
                style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.SemiBold),
                onClick = { onEvent(UserInfoEvent.TermsClicked) }
            )
            Text(
                text = stringResource(id = R.string.user_info_agreement_and_content),
                style = MaterialTheme.typography.body2
            )
        }
        Row {
            ClickableText(
                text = R.string.user_info_agreement_privacy_content,
                style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.SemiBold),
                onClick = { onEvent(UserInfoEvent.PrivacyPolicyClicked) }
            )
            Text(
                text = stringResource(id = R.string.user_info_agreement_dot_content),
                style = MaterialTheme.typography.body2
            )
        }
    }
}


@Preview(
    name = "UserInfoScreen(LightMode)",
    showBackground = true,
    device = Devices.PIXEL_2
)
@Preview(
    name = "UserInfoScreen(DarkMode)",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_2
)
@Composable
fun LoginScreenPreview() {
    ChatAppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            UserInfoScreen(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                launchActivity = {},
            )
        }
    }
}

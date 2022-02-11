package com.android.chatapp.feature_agreement.presentation.privacy_policy

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.android.chatapp.R
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.feature_agreement.domain.model.Agreement
import com.android.chatapp.feature_agreement.domain.model.extractAgreements
import com.android.chatapp.feature_agreement.presentation.components.AgreementItem
import com.android.chatapp.feature_agreement.presentation.components.AgreementScreen
import com.android.chatapp.feature_authentication.presentation.user_info.components.ClickableText
import com.android.chatapp.ui.theme.ChatAppTheme

private const val PLAY_PRIVACY_POLICY =
    "https://www.google.com/policies/privacy/"
private const val AD_MOB_PRIVACY_POLICY =
    "https://support.google.com/admob/answer/6128543?hl=en"
private const val FIREBASE_ANALYTICS_PRIVACY_POLICY =
    "https://firebase.google.com/policies/analytics"

private fun Context.launchBrowser(url: String) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(browserIntent)
}

@Composable
fun PrivacyPolicyScreen(
    modifier: Modifier = Modifier,
    backPressed: () -> Unit
) {
    val policies = remember { mutableStateListOf<Agreement>() }
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        policies extractAgreements context.resources.getStringArray(R.array.agreement_privacy_policy)
    }
    AgreementScreen(
        modifier = modifier,
        title = R.string.privacy_policy_title,
        caption = R.string.privacy_policy_content,
        image = R.drawable.privacy_policy_ic,
        description = R.string.privacy_policy_showcase_desc,
        agreements = policies,
        backPressed = backPressed
    ) { paddingModifier ->
        item {
            AgreementItem(
                modifier = paddingModifier,
                title = stringResource(id = R.string.privacy_policy_information_title),
                caption = stringResource(id = R.string.privacy_policy_information_content),
            ) {
                val style = MaterialTheme.typography.body2.copy(fontSize = 15.sp)
                ClickableText(
                    text = R.string.privacy_policy_information_play_link,
                    style = style
                ) { context.launchBrowser(PLAY_PRIVACY_POLICY) }
                ClickableText(
                    text = R.string.privacy_policy_information_ad_mob_link,
                    style = style
                ) { context.launchBrowser(AD_MOB_PRIVACY_POLICY) }
                ClickableText(
                    text = R.string.privacy_policy_information_firebase_link,
                    style = style
                ) { context.launchBrowser(FIREBASE_ANALYTICS_PRIVACY_POLICY) }
            }
        }
    }
}

@Preview(
    name = "PrivacyPolicyScreen(LightMode)",
    showBackground = true,
)
@Preview(
    name = "PrivacyPolicyScreen(DarkMode)",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun PrivacyPolicyScreenPreview() {
    ChatAppTheme {
        Surface {
            PrivacyPolicyScreen(
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium),
                backPressed = {}
            )
        }
    }
}
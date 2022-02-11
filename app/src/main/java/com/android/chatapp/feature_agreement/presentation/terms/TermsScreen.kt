package com.android.chatapp.feature_agreement.presentation.terms

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.android.chatapp.R
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.feature_agreement.domain.model.Agreement
import com.android.chatapp.feature_agreement.domain.model.extractAgreements
import com.android.chatapp.feature_agreement.presentation.components.AgreementScreen
import com.android.chatapp.ui.theme.ChatAppTheme


@Composable
fun TermsScreen(
    modifier: Modifier = Modifier,
    backPressed: () -> Unit
) {
    val terms = remember { mutableStateListOf<Agreement>() }
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        terms extractAgreements context.resources.getStringArray(R.array.agreement_terms)
    }
    AgreementScreen(
        modifier = modifier,
        title = R.string.terms_of_service_title,
        caption = R.string.terms_introduction_content,
        image = R.drawable.privacy_policy_ic,
        description = R.string.privacy_policy_showcase_desc,
        agreements = terms,
        backPressed = backPressed
    )
}

@Preview(
    name = "TermsScreen(LightMode)",
    showBackground = true,
)
@Preview(
    name = "TermsScreen(DarkMode)",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun TermsScreenPreview() {
    ChatAppTheme {
        Surface {
            TermsScreen(
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium),
                backPressed = {}
            )
        }
    }
}
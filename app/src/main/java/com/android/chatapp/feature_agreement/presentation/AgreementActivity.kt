package com.android.chatapp.feature_agreement.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.feature_agreement.presentation.privacy_policy.PrivacyPolicyScreen
import com.android.chatapp.feature_agreement.presentation.terms.TermsScreen
import com.android.chatapp.ui.theme.ChatAppTheme
import dagger.hilt.android.AndroidEntryPoint

private const val AGREEMENT_SCREEN = "agreement_screen"

val Context.privacyPolicy
    get() = startActivity(Intent(this, AgreementActivity::class.java).apply {
        putExtra(AGREEMENT_SCREEN, Screen.PRIVACY_POLICY)
    })

val Context.terms
    get() = startActivity(Intent(this, AgreementActivity::class.java).apply {
        putExtra(AGREEMENT_SCREEN, Screen.TERMS)
    })


@AndroidEntryPoint
class AgreementActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgreementApp(screen = intent.getSerializableExtra(AGREEMENT_SCREEN) as? Screen) {
                finish()
            }
        }
    }
}

@Composable
fun AgreementApp(screen: Screen?, backPressed: () -> Unit) {
    ChatAppTheme {
        val modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium)
        when (screen) {
            Screen.PRIVACY_POLICY, null -> PrivacyPolicyScreen(
                modifier = modifier,
                backPressed = backPressed
            )
            Screen.TERMS -> TermsScreen(
                modifier = modifier,
                backPressed = backPressed
            )
        }
    }
}
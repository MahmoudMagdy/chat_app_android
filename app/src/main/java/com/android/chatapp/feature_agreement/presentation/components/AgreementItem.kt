package com.android.chatapp.feature_agreement.presentation.components

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.chatapp.R
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.feature_agreement.domain.model.Agreement
import com.android.chatapp.ui.theme.ChatAppTheme




@Composable
fun AgreementItem(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    @StringRes caption: Int,
) {
    AgreementItem(
        modifier,
        stringResource(id = title),
        stringResource(caption)
    )
}

@Composable
fun AgreementItem(
    modifier: Modifier = Modifier,
    agreement: Agreement
) {
    AgreementItem(
        modifier,
        agreement.title,
        agreement.caption
    )
}

@Composable
fun AgreementItem(
    modifier: Modifier = Modifier,
    title: String,
    caption: String,
    content: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            modifier = Modifier.padding(vertical = 12.dp),
            text = title,
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
        )
        Card(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            backgroundColor = MaterialTheme.colors.secondaryVariant
        ) {
            Column(modifier = Modifier.padding(all = 12.dp)) {
                Text(
                    text = caption,
                    style = MaterialTheme.typography.subtitle1,
                    textAlign = TextAlign.Start,
                )
                content?.invoke()
            }
        }
    }
}

@Preview(
    name = "AgreementTitle(LightMode)",
    showBackground = true,
)
@Preview(
    name = "AgreementTitle(DarkMode)",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun AgreementTitlePreview() {
    ChatAppTheme {
        Surface {
            AgreementItem(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                title = R.string.privacy_policy_title,
                caption = R.string.privacy_policy_content,
            )
        }
    }
}
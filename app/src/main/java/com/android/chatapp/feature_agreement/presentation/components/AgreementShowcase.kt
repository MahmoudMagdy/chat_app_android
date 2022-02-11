package com.android.chatapp.feature_agreement.presentation.components

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.chatapp.R
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.ui.theme.ChatAppTheme


@Composable
fun AgreementShowcase(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    @StringRes content: Int,
    @DrawableRes image: Int,
    @StringRes description: Int,
) {
    AgreementShowcase(
        modifier,
        stringResource(id = title),
        stringResource(content),
        painterResource(id = image),
        stringResource(description)
    )
}

@Composable
fun AgreementShowcase(
    modifier: Modifier = Modifier,
    title: String,
    content: String,
    painter: Painter,
    description: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center,
        )
        Text(
            modifier = Modifier.padding(vertical = 10.dp),
            text = content,
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center
        )
        Image(
            modifier = Modifier.size(300.dp),
            painter = painter,
            contentDescription = description
        )
    }
}

@Preview(
    name = "AgreementShowcase(LightMode)",
    showBackground = true,
)
@Preview(
    name = "AgreementShowcase(DarkMode)",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun AgreementShowcasePreview() {
    ChatAppTheme {
        Surface {
            AgreementShowcase(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                title = R.string.privacy_policy_title,
                content = R.string.privacy_policy_content,
                image = R.drawable.privacy_policy_ic,
                description = R.string.privacy_policy_showcase_desc
            )
        }
    }
}
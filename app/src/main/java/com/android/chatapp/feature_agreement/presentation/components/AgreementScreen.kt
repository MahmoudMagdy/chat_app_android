package com.android.chatapp.feature_agreement.presentation.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NavigateBefore
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.chatapp.R
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.feature_agreement.domain.model.Agreement


@Composable
fun AgreementScreen(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    @StringRes caption: Int,
    @DrawableRes image: Int,
    @StringRes description: Int,
    agreements: List<Agreement>,
    backPressed: () -> Unit,
    content: (LazyListScope.(paddingModifier: Modifier) -> Unit)? = null
) {
    Box {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                AgreementShowcase(
                    modifier = modifier.padding(top = MaterialTheme.spacing.large),
                    title = title,
                    content = caption,
                    image = image,
                    description = description
                )
            }
            items(agreements) { item ->
                AgreementItem(
                    modifier = modifier,
                    agreement = item
                )
            }
            if (content != null) content(modifier)
            item {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            }
        }
        Icon(
            modifier = Modifier
                .align(Alignment.TopStart)
                .clickable(onClick = backPressed)
                .padding(all = MaterialTheme.spacing.extraMedium)
                .background(MaterialTheme.colors.primary.copy(alpha = 0.8f), CircleShape)
                .padding(all = 4.dp),
            imageVector = Icons.Outlined.NavigateBefore,
            contentDescription = stringResource(id = R.string.gen_back_btn_desc),
            tint = MaterialTheme.colors.onPrimary
        )
    }
}

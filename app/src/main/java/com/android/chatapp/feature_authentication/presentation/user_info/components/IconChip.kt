package com.android.chatapp.feature_authentication.presentation.user_info.components

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.chatapp.R
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.ui.theme.ChatAppTheme

class IconChipState(
    @StringRes val text: Int,
    initialSelected: Boolean = false,
    initialEnabled: Boolean = true,
) {

    var selected by mutableStateOf(initialSelected)
        private set
    var enabled by mutableStateOf(initialEnabled)
        private set

    val select get() = run { selected = true }
    val unSelect get() = run { selected = false }

    fun setSelection(selected: Boolean) {
        this.selected = selected
    }

    fun setEnabledValue(value: Boolean) {
        enabled = value
    }

    companion object {
        val Saver: Saver<IconChipState, *> = listSaver(
            save = {
                it.run {
                    listOf(text.toString(), selected.toString(), enabled.toString())
                }
            },
            restore = {
                IconChipState(
                    text = it[0].toInt(),
                    initialSelected = it[1].toBooleanStrict(),
                    initialEnabled = it[2].toBooleanStrict(),
                )
            }
        )
    }
}

@Composable
fun rememberIconChipState(@StringRes text: Int, selected: Boolean = false) =
    rememberSaveable(text, selected, saver = IconChipState.Saver) {
        IconChipState(text, selected)
    }


@Composable
fun IconChip(
    modifier: Modifier = Modifier,
    state: IconChipState,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (state.selected)
            MaterialTheme.colors.primary.copy(
                alpha = 0.9f,
                red = 0.7f,
                blue = 0.7f,
                green = 0.5f
            )
        else Color.LightGray
//        else Color.Gray.copy(alpha = 0.9f, blue = 0.55f, green = 0.56f)
    )
    Row(
        modifier = modifier
            .padding(vertical = MaterialTheme.spacing.small)
            .background(
                color = bgColor,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(
                vertical = 6.dp,
                horizontal = 10.dp
            )
            .clickable(enabled = state.enabled, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .padding(end = MaterialTheme.spacing.extraSmall)
                .size(20.dp)
        ) {
            icon()
        }
        Text(
            text = stringResource(id = state.text),
            color = Color.Black,
            style = MaterialTheme.typography.body2
        )
    }
}


@Preview(
    name = "IconChip(LightMode)",
    showBackground = true,
)
@Preview(
    name = "IconChip(DarkMode)",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun IconChipPreview() {
    ChatAppTheme {
        Surface {
            IconChip(
                state = rememberIconChipState(
                    text = R.string.user_info_gender_female_title,
                    selected = true
                ),
                onClick = {}
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user_info_gender_female),
                    contentDescription = stringResource(id = R.string.user_info_gender_female_title),
                )
            }
        }
    }
}
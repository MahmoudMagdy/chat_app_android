package com.android.chatapp.feature_authentication.presentation.user_info.components

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.android.chatapp.R
import com.android.chatapp.core.presentation.util.EMPTY_TEXT
import com.android.chatapp.ui.theme.ChatAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val ITEM_NOT_EXISTS = -1

class DropDownMenuInputState(
    @StringRes val hint: Int,
    options: List<String> = listOf(),
    initialExpanded: Boolean = false,
    initialEnabled: Boolean = true,
) {

    private val _selectedOption = MutableStateFlow(EMPTY_TEXT)
    val selectedOption get() = _selectedOption.asStateFlow()

    var expanded by mutableStateOf(initialExpanded)
        private set
    var enabled by mutableStateOf(initialEnabled)
        private set

    val toggleExpand get() = run { expanded = !expanded }
    val unExpand get() = run { expanded = false }

    private val _options = MutableStateFlow(options)
    val options get() = _options.asStateFlow()

    val selectedOptionPosition
        get() = getItemPosition(_selectedOption.value)

    private fun getItemPosition(value: String) = _options.value.indexOf(value)

    fun setEnabledValue(value: Boolean) {
        enabled = value
    }

    fun setOptions(options: List<String>) {
        _options.value = options
    }

    fun setSelectedOptionValue(value: String) {
        unExpand
        _selectedOption.value = value
    }

    fun onSelectionChanged(
        scope: CoroutineScope,
        collector: (item: String, position: Int) -> Unit
    ) {
        scope.launch {
            selectedOption.collectLatest { collector(it, getItemPosition(it)) }
        }
    }

    fun removeSelection() {
        _selectedOption.value = EMPTY_TEXT
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DropDownMenuInput(
    modifier: Modifier = Modifier,
    state: DropDownMenuInputState,
) {
    val options by state.options.collectAsState()
    val selectedOption by state.selectedOption.collectAsState()
    // We want to react on tap/press on TextField to show menu
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = state.expanded,
        onExpandedChange = { state.toggleExpand }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedOption,
            onValueChange = { },
            label = { Text(stringResource(id = state.hint)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.expanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            enabled = state.enabled
        )
        ExposedDropdownMenu(
            expanded = state.expanded,
            onDismissRequest = { state.unExpand }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = { state.setSelectedOptionValue(selectionOption) }
                ) {
                    Text(text = selectionOption)
                }
            }
        }
    }
}


@Preview(
    name = "DropDownMenuInput(LightMode)",
    showBackground = true,
)
@Preview(
    name = "DropDownMenuInput(DarkMode)",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun DropDownMenuInputPreview() {
    ChatAppTheme {
        Surface {
            DropDownMenuInput(
                state = DropDownMenuInputState(
                    hint = R.string.user_info_month_title,
                    initialExpanded = true
                )
            )
        }
    }
}
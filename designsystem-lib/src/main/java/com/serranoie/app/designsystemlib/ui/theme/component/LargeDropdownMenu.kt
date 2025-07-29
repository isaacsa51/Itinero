/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: LargeDropdownMenu.kt
 - Project: Itinero
 - Module: Itinero.designsystem-lib.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 08 julio 2025
 */

package com.serranoie.app.designsystemlib.ui.theme.component

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import com.serranoie.app.designsystemlib.ui.utils.Constants.ALPHA_DISABLED
import com.serranoie.app.designsystemlib.ui.utils.Constants.ALPHA_FULL
import com.serranoie.app.designsystemlib.ui.utils.Constants.basePadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.commonCornerRadius
import com.serranoie.app.designsystemlib.ui.utils.Constants.dropdownMenuEndPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.dropdownMenuIconPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.dropdownMenuIconTopPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.dropdownMenuItemPadding

class LargeDropdownMenu {
    @SuppressLint("NotConstructor")
    @Composable
    fun <T> LargeDropdownMenu(
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        label: String,
        notSetLabel: String? = null,
        items: List<T>,
        selectedIndex: Int = -1,
        onItemSelected: (index: Int, item: T) -> Unit,
        selectedItemToString: (T) -> String = { it.toString() },
        drawItem: @Composable (T, Boolean, Boolean, () -> Unit) -> Unit = { item, selected, itemEnabled, onClick ->
            LargeDropdownMenuItem(
                text = item.toString(),
                selected = selected,
                enabled = itemEnabled,
                onClick = onClick,
            )
        },
    ) {
        var expanded by remember { mutableStateOf(false) }

        Box(modifier = modifier.height(IntrinsicSize.Min)) {
            ITextField(
                value = items.getOrNull(selectedIndex)?.let { selectedItemToString(it) } ?: "",
                onValueChange = { },
                label = label,
                placeholder = "",
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = null
            )

            // Trailing icon overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = dropdownMenuEndPadding)
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = dropdownMenuIconPadding, top = dropdownMenuIconTopPadding),
                    tint = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outline
                )
            }

            // Transparent clickable surface on top
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.extraSmall)
                    .clickable(enabled = enabled) { expanded = true },
                color = Color.Transparent,
            ) {}
        }

        if (expanded) {
            Dialog(
                onDismissRequest = { expanded = false },
            ) {
                Surface(
                    shape = RoundedCornerShape(commonCornerRadius),
                ) {
                    val listState = rememberLazyListState()
                    if (selectedIndex > -1) {
                        LaunchedEffect("ScrollToSelected") {
                            listState.scrollToItem(index = selectedIndex)
                        }
                    }

                    LazyColumn(modifier = Modifier.fillMaxWidth(), state = listState) {
                        if (notSetLabel != null) {
                            item {
                                LargeDropdownMenuItem(
                                    text = notSetLabel,
                                    selected = false,
                                    enabled = false,
                                    onClick = { },
                                )
                            }
                        }
                        itemsIndexed(items) { index, item ->
                            val selectedItem = index == selectedIndex
                            drawItem(
                                item, selectedItem, true
                            ) {
                                onItemSelected(index, item)
                                expanded = false
                            }

                            if (index < items.lastIndex) {
                                HorizontalDivider(modifier = Modifier.padding(horizontal = basePadding))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LargeDropdownMenuItem(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val contentColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = ALPHA_DISABLED)
        selected -> MaterialTheme.colorScheme.primary.copy(alpha = ALPHA_FULL)
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = ALPHA_FULL)
    }

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Box(
            modifier = Modifier
                .clickable(enabled) { onClick() }
                .fillMaxWidth()
                .padding(dropdownMenuItemPadding)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

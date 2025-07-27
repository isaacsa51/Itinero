/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ITimePicker.kt
 - Project: Itinero
 - Module: Itinero.designsystem-lib.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 26 junio 2025
 */

package com.serranoie.app.designsystemlib.ui.theme.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.serranoie.app.designsystemlib.ui.utils.Constants.basePadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.mediumPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.timePickerDialogElevation
import com.serranoie.app.designsystemlib.ui.utils.Constants.timePickerHeight

@Composable
fun ITimePicker(
    dialogTitle: String = "Select a date",
    onDismissed: () -> Unit,
    onConfirmButtonClicked: @Composable (() -> Unit),
    onDismissButtonClicked: @Composable (() -> Unit)? = null,
    timePickerContent: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissed,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = timePickerDialogElevation,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = AlertDialogDefaults.containerColor,
                ),
            color = AlertDialogDefaults.containerColor,
        ) {
            Column(
                modifier = Modifier.padding(mediumPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = basePadding),
                    text = dialogTitle,
                    style = MaterialTheme.typography.labelMedium,
                )
                timePickerContent()
                Row(
                    modifier = Modifier
                        .height(timePickerHeight)
                        .fillMaxWidth(),
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    onDismissButtonClicked?.invoke()
                    onConfirmButtonClicked()
                }
            }
        }
    }
}

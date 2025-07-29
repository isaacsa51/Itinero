/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: Constants.kt
 - Project: Itinero
 - Module: Itinero.designsystem-lib.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 26 junio 2025
 */

package com.serranoie.app.designsystemlib.ui.utils

import androidx.compose.ui.unit.dp

object Constants {

    // Spacing & Padding
    val extraSmallPadding = 4.dp
    val smallPadding = 8.dp
    val basePadding = 16.dp
    val mediumPadding = 24.dp
    val largePadding = 36.dp

    // Dimensions & Sizes
    val commonCornerRadius = 8.dp
    val borderStrokeWidth = 1.dp
    val iconSize = 24.dp
    val smallIconSize = 14.dp
    val indicatorSize = 14.dp
    val pageIndicatorWidth = 52.dp
    val sliderHeight = 56.dp

    // Elevation & Shadows
    val subtleElevation = 2.dp
    val basicElevation = 4.dp
    val ticketShadowElevation = 2.dp

    // Alpha Values
    const val DISABLED_ALPHA = 0.6f
    const val PRIMARY_ALPHA = 0.75f
    const val SURFACE_DISABLED_ALPHA = 0.12f
    const val PLACEHOLDER_ALPHA = 0.6f
    const val DISABLED_OUTLINED_BUTTON_BORDER_ALPHA = 0.12f
    const val DISABLED_BUTTON_CONTENT_ALPHA = 0.38f

    // Animation Constants
    const val CONTENT_ANIMATION_DURATION = 300
    const val STRIKETHROUGH_CHAR_DURATION = 30
    const val STRIKETHROUGH_MAX_DURATION = 600

    // Buttons
    val buttonHeight = 48.dp
    val buttonSmallHeight = 32.dp
    val buttonLargeHeight = 56.dp
    val buttonIconSize = 24.dp

    // Input Fields
    val textFieldSmallHeight = 32.dp
    val textFieldIconSize = 16.dp
    val textFieldIconSpacing = 8.dp
    val textFieldHorizontalPadding = 12.dp
    val textFieldVerticalPadding = 4.dp

    // Date & Time Components
    val timePickerHeight = 40.dp
    val timePickerDialogElevation = 6.dp
    val dateTimeInputIconSize = 20.dp
    val dateTimeInputIconSpacing = 12.dp
    const val DATE_RANGE_TOOLBAR_DEFAULT_WEIGHT = 0.15f

    // Bottom Sheets
    val bottomSheetTitleSpacing = 8.dp
    val bottomSheetContentSpacing = 24.dp
    val bottomSheetButtonSpacing = 12.dp
    val bottomSheetCloseSpacing = 16.dp
    val bottomSheetIconSpacing = 4.dp

    // Dropdown Menus
    val dropdownMenuEndPadding = 12.dp
    val dropdownMenuIconPadding = 4.dp
    val dropdownMenuIconTopPadding = 8.dp
    val dropdownMenuItemPadding = 16.dp

    // Location input constants
    val locationInputLabelStartPadding = 16.dp
    val locationInputLabelBottomPadding = 4.dp
    val locationInputRowVerticalPadding = 4.dp
    val locationInputIconSpacing = 12.dp
    val locationInputIconAlpha = 0.8f

    // OTP Inputs
    val otpCharWidth = 34.dp
    val otpCharPadding = 4.dp
    val otpCharCornerRadius = 4.dp
    val otpCharSpacing = 4.dp
    val otpDashVerticalPadding = 2.dp
    const val OTP_DEFAULT_COUNT = 5

    // Balance Circles
    val balanceCircleMinSize = 120.dp
    val balanceCircleMaxSize = 160.dp
    val balanceCircleDefaultSize = 140.dp
    val balanceCircleOverlapOffset = 60.dp
    const val BALANCE_CIRCLE_SIZE_RATIO_THRESHOLD = 0.1
    const val BALANCE_CIRCLE_MAX_RATIO = 1.5
    const val BALANCE_CIRCLE_RATIO_SCALE = 0.5
    const val BALANCE_CIRCLE_MIN_AMOUNT = 0.01

    // Record Button
    val recordButtonMinSize = 56.dp
    val recordButtonMinHeight = 6.dp
    val recordButtonPadding = 18.dp
    val recordButtonSwipeThreshold = 200.dp
    val recordButtonVerticalThreshold = 80.dp
    const val RECORD_BUTTON_SCALE_RECORDING = 2f
    const val RECORD_BUTTON_SCALE_IDLE = 1f
    const val RECORD_BUTTON_ALPHA_RECORDING = 1f
    const val RECORD_BUTTON_ALPHA_IDLE = 0f
    const val RECORD_BUTTON_ANIMATION_DURATION = 2000
    const val RECORD_BUTTON_COLOR_ANIMATION_DURATION = 200

    // Select Fields
    val selectFieldLabelStartPadding = 16.dp
    val selectFieldLabelBottomPadding = 4.dp
    val selectFieldContentPadding = 16.dp
    val selectFieldIconSpacing = 12.dp
    val selectFieldElevation = 4.dp

    // Settings Groups
    val settingsGroupContainerPadding = 16.dp
    val settingsGroupVerticalPadding = 8.dp
    val settingsGroupTitleBottomPadding = 10.dp
    val settingsGroupItemPadding = 16.dp
    val settingsGroupItemVerticalPadding = 20.dp
    val settingsGroupIconSpacing = 16.dp
    val settingsGroupItemSpacing = 2.dp
    val settingsGroupElevation = 4.dp
    val settingsGroupExpandedPadding = 10.dp
    val settingsGroupDividerPadding = 8.dp
    val settingsGroupSmallCornerRadius = 1.dp

    // Slide to Confirm
    val slideToConfirmHeight = 64.dp
    val slideToConfirmIndicatorPadding = 4.dp
    val slideToConfirmCornerRadius = 8.dp
    val slideToConfirmIconSize = 36.dp
    val slideToConfirmDoneIconSize = 44.dp
    const val SLIDE_TO_CONFIRM_ANIMATION_DURATION = 300
    const val SLIDE_TO_CONFIRM_THRESHOLD = 0.3f

    const val ALPHA_DISABLED = 0.3f
    const val ALPHA_FULL = 1f
}
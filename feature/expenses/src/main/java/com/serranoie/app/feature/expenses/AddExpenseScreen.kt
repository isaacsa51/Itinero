package com.serranoie.app.feature.expenses

import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material.icons.outlined.SyncAlt
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Percent
import androidx.compose.material.icons.rounded.SyncAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystemlib.ui.DevicePreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.theme.component.AnimatedStrikethroughText
import com.serranoie.app.designsystemlib.ui.theme.component.ButtonImportance
import com.serranoie.app.designsystemlib.ui.theme.component.DateTimeInput
import com.serranoie.app.designsystemlib.ui.theme.component.DateTimeInputType
import com.serranoie.app.designsystemlib.ui.theme.component.IButton
import com.serranoie.app.designsystemlib.ui.theme.component.IFilledSmallerTextField
import com.serranoie.app.designsystemlib.ui.theme.component.ITextField
import com.serranoie.app.designsystemlib.ui.theme.component.LargeDropdownMenu
import com.serranoie.app.designsystemlib.ui.theme.component.card.TicketView
import com.serranoie.app.designsystemlib.ui.utils.Constants.basePadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.borderStrokeWidth
import com.serranoie.app.designsystemlib.ui.utils.Constants.commonCornerRadius
import com.serranoie.app.designsystemlib.ui.utils.Constants.extraSmallPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.iconSize
import com.serranoie.app.designsystemlib.ui.utils.Constants.largePadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.mediumPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.smallPadding
import com.serranoie.app.designsystemlib.ui.utils.Utils.toggleFeedback
import com.serranoie.app.feature.expenses.util.ExpenseCategory
import com.serranoie.app.feature.expenses.util.icon
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.math.abs

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    navController: NavController,
    expenseState: ExpenseDetailsViewModel.ExpenseState,
    formUiState: ExpenseDetailsViewModel.UIState,
    splitType: SplitType,
    groupMembers: List<GroupMember>,
    persons: List<String>,
    snackbarHostState: SnackbarHostState,
    onExpenseNameChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onShowDatePicker: (Boolean) -> Unit,
    onCategoryChange: (ExpenseCategory) -> Unit,
    onShowCategoryDropdownChange: (Boolean) -> Unit,
    onPaidByChange: (String?) -> Unit,
    onPaymentMethodChange: (String) -> Unit,
    onSplitTypeChange: (SplitType) -> Unit,
    onToggleMemberIncluded: (Int, Boolean) -> Unit,
    onUpdateMemberPercentage: (Int, Int) -> Unit,
    onUpdateMemberAmount: (Int, Double) -> Unit,
    onNotesChange: (String) -> Unit,
    onExtraInfoChange: (String) -> Unit,
    onSaveExpense: () -> Unit,
    onClearErrorMessage: () -> Unit,
    onClearSuccessMessage: () -> Unit,
    onDateSelected: (String) -> Unit,
    isPercentageValid: Boolean,
    isManualAmountValid: Boolean
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    if (formUiState.showSuccessMessage) {
        LaunchedEffect(Unit) {
            onClearSuccessMessage()
            navController.previousBackStackEntry?.savedStateHandle?.set(
                    "expense_saved_message",
                    "Expense saved successfully!"
                )
            navController.popBackStack()
        }
    }

    if (formUiState.showDatePicker) {
        DatePickerDialog(
            onDismiss = { onShowDatePicker(false) }, onDateSelected = onDateSelected
        )
    }

    if (formUiState.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { },
            icon = { Icon(Icons.Default.Error, contentDescription = null) },
            title = { Text("Error") },
            text = { Text(formUiState.errorMessage) },
            confirmButton = {
                TextButton(onClick = {
                    onClearErrorMessage()
                }) {
                    Text("OK")
                }
            })
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                title = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = expenseState.name.ifEmpty { "New expense" },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (expenseState.amount.isNotEmpty()) {
                            Text(
                                text = "$ ${expenseState.amount}",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    IButton(
                        onClick = { onSaveExpense() },
                        enabled = !expenseState.isSaving,
                        modifier = Modifier
                            .padding(end = smallPadding),
                        height = ButtonDefaults.ExtraSmallContainerHeight,
                        contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.ExtraSmallContainerHeight)
                    ) {
                        if (expenseState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(ButtonDefaults.iconSizeFor(ButtonDefaults.ExtraSmallContainerHeight)),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Spacer(
                                modifier = Modifier.size(
                                    ButtonDefaults.iconSpacingFor(
                                        ButtonDefaults.ExtraSmallContainerHeight
                                    )
                                )
                            )
                            Text(if (expenseState.isSaving) "Saving..." else "Save")
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(smallPadding)
        ) {
            item {
                ExpenseBasicDetailsSection(
                    expenseName = expenseState.name,
                    expenseNameError = expenseState.nameError,
                    onExpenseNameChange = onExpenseNameChange,
                    amount = expenseState.amount,
                    amountError = expenseState.amountError,
                    onAmountChange = onAmountChange,
                    date = expenseState.date,
                    onShowDatePicker = { onShowDatePicker(true) },
                    onDateSelected = onDateSelected,
                    category = expenseState.category,
                    onCategoryChange = onCategoryChange,
                    showCategoryDropdown = formUiState.showCategoryDropdown,
                    onShowCategoryDropdownChange = onShowCategoryDropdownChange,
                )
            }

            item {
                PaymentDetailsSection(
                    paidBy = expenseState.paidBy ?: "",
                    onPaidByChange = onPaidByChange,
                    persons = persons,
                    paymentMethod = expenseState.paymentMethod,
                    onPaymentMethodChange = onPaymentMethodChange,
                    extraInfo = expenseState.extraInfo ?: "",
                    onExtraInfoChange = onExtraInfoChange
                )
            }

            item {
                SplitDetailsSection(
                    splitType = splitType,
                    onSplitTypeChange = onSplitTypeChange,
                    groupMembers = groupMembers,
                    onToggleMemberIncluded = onToggleMemberIncluded,
                    onUpdateMemberPercentage = onUpdateMemberPercentage,
                    onUpdateMemberAmount = onUpdateMemberAmount,
                    amount = expenseState.amount,
                    paidBy = expenseState.paidBy ?: "",
                    isPercentageValid = isPercentageValid,
                    isManualAmountValid = isManualAmountValid
                )
            }

            item {
                expenseState.notes?.let {
                    NotesSection(
                        notes = it, onNotesChange = onNotesChange
                    )
                }
            }
        }
    }
}

@Composable
fun ExpenseBasicDetailsSection(
    expenseName: String,
    expenseNameError: String?,
    onExpenseNameChange: (String) -> Unit,
    amount: String,
    amountError: String?,
    onAmountChange: (String) -> Unit,
    date: String,
    onShowDatePicker: () -> Unit,
    onDateSelected: (String) -> Unit,
    category: ExpenseCategory,
    onCategoryChange: (ExpenseCategory) -> Unit,
    showCategoryDropdown: Boolean,
    onShowCategoryDropdownChange: (Boolean) -> Unit,
) {

    Column(
        modifier = Modifier.padding(horizontal = basePadding, vertical = 0.dp),
        verticalArrangement = Arrangement.spacedBy(smallPadding)
    ) {
        ITextField(
            modifier = Modifier.fillMaxWidth(),
            value = expenseName,
            onValueChange = onExpenseNameChange,
            label = "Expense Name",
            placeholder = "e.g., Dinner at Restaurant",
            leadingIcon = Icons.Default.Description,
            borderColor = if (expenseNameError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant,
            keyboardOptions = KeyboardOptions.Default
        )
        expenseNameError?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        ITextField(
            modifier = Modifier.fillMaxWidth(),
            value = amount,
            onValueChange = onAmountChange,
            label = "Amount",
            placeholder = "",
            leadingIcon = Icons.Default.AttachMoney,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        amountError?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        DateTimeInput(
            selectedDateTime = if (date.isNotEmpty()) {
                try {
                    val localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
                    Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                } catch (e: Exception) {
                    Log.e("ITINERO - AddExpense", "Failed to parse date: $date", e)
                    null
                }
            } else null,
            onDateTimeSelected = { selectedDate ->
                val dateFormat = DateTimeFormatter.ISO_LOCAL_DATE
                val localDate =
                    Instant.ofEpochMilli(selectedDate.time).atZone(ZoneId.systemDefault())
                        .toLocalDate()
                onDateSelected(localDate.format(dateFormat))
            },
            label = "Date: ",
            enabled = true,
            inputType = DateTimeInputType.DATE,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = Icons.Rounded.CalendarToday,
        )

        val categories = ExpenseCategory.entries.sortedBy { it.displayName }
        val selectedCategoryIndex = categories.indexOf(category)

        LargeDropdownMenu().LargeDropdownMenu(
            label = "Category",
            items = categories,
            selectedIndex = selectedCategoryIndex,
            onItemSelected = { index, item ->
                onCategoryChange(item)
            },
            selectedItemToString = { it.displayName },
            drawItem = { item, selected, enabled, onClick ->
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled) { onClick() }
                    .padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = item.icon(),
                            contentDescription = null,
                            modifier = Modifier.size(iconSize),
                            tint = if (selected) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                        Spacer(modifier = Modifier.width(mediumPadding))
                        Text(
                            text = item.displayName,
                            color = if (selected) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PaymentDetailsSection(
    paidBy: String,
    onPaidByChange: (String?) -> Unit,
    persons: List<String>,
    paymentMethod: String,
    onPaymentMethodChange: (String) -> Unit,
    extraInfo: String,
    onExtraInfoChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = basePadding, vertical = 0.dp)
    ) {
        val view = LocalView.current

        Text(
            text = "PAYMENT DETAILS",
            style = MaterialTheme.typography.labelLargeEmphasized,
            color = MaterialTheme.colorScheme.outline,
        )

        Column {
            val selectedPersonIndex = persons.indexOf(paidBy).takeIf { it >= 0 } ?: 0

            LargeDropdownMenu().LargeDropdownMenu(
                label = "Paid By",
                items = persons,
                selectedIndex = selectedPersonIndex,
                onItemSelected = { index, person ->
                    onPaidByChange(person)
                },
                selectedItemToString = { it },
                drawItem = { person, selected, enabled, onClick ->
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled) { onClick() }
                        .padding(mediumPadding)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(iconSize),
                                tint = if (selected) MaterialTheme.colorScheme.primary else LocalContentColor.current
                            )
                            Spacer(modifier = Modifier.width(mediumPadding))
                            Text(
                                text = person,
                                style = MaterialTheme.typography.titleSmall,
                                color = if (selected) MaterialTheme.colorScheme.primary else LocalContentColor.current
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(smallPadding))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            ) {
                val paymentOptions = listOf("Cash", "Debit Card", "Credit Card", "Custom")
                val selectedPaymentIndex =
                    paymentOptions.indexOf(paymentMethod).takeIf { it >= 0 } ?: 0

                val customShapes = ToggleButtonShapes(
                    shape = RoundedCornerShape(commonCornerRadius),
                    pressedShape = RoundedCornerShape(basePadding),
                    checkedShape = RoundedCornerShape(basePadding)
                )

                paymentOptions.forEachIndexed { index, method ->
                    ToggleButton(
                        checked = selectedPaymentIndex == index,
                        onCheckedChange = { checked ->
                            if (checked) {
                                onPaymentMethodChange(method)
                                view.toggleFeedback()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(basePadding * 3),
                        shapes = customShapes,
                        colors = ToggleButtonDefaults.toggleButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            checkedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            checkedContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        border = BorderStroke(
                            width = borderStrokeWidth,
                            color = if (selectedPaymentIndex == index) MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.5f
                            )
                            else MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Text(
                            text = method,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(mediumPadding))

        ITextField(
            value = extraInfo,
            onValueChange = onExtraInfoChange,
            label = "Extra information",
            placeholder = "Enter any extra information about this expense",
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp)
        )

        Spacer(modifier = Modifier.height(smallPadding))

        IButton(
            onClick = {/*TODO: Launch bottom modal to select media or camera */ },
            modifier = Modifier.fillMaxWidth(),
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.PhotoCamera,
                        contentDescription = null,
                        modifier = Modifier.size(iconSize),
                        tint = LocalContentColor.current
                    )
                    Spacer(modifier = Modifier.width(smallPadding))
                    Text("Add Receipt")
                }
            },
            importance = ButtonImportance.Tertiary
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SplitDetailsSection(
    splitType: SplitType,
    onSplitTypeChange: (SplitType) -> Unit,
    groupMembers: List<GroupMember>,
    onToggleMemberIncluded: (Int, Boolean) -> Unit,
    onUpdateMemberPercentage: (Int, Int) -> Unit,
    onUpdateMemberAmount: (Int, Double) -> Unit,
    amount: String,
    paidBy: String,
    isPercentageValid: Boolean,
    isManualAmountValid: Boolean
) {
    Column(
        modifier = Modifier.padding(horizontal = basePadding, vertical = 0.dp)
    ) {
        Text(
            text = "SPLIT DETAILS",
            style = MaterialTheme.typography.labelLargeEmphasized,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(bottom = smallPadding)
        )

        SplitTypeSelector(
            splitType = splitType, onSplitTypeChange = onSplitTypeChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        TicketView(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(mediumPadding),
            ) {
                SplitMembersHeader(splitType = splitType)

                HorizontalDivider()

                groupMembers.forEachIndexed { index, member ->
                    MemberRow(
                        member = member,
                        index = index,
                        splitType = splitType,
                        paidBy = paidBy,
                        onToggleIncluded = { included -> onToggleMemberIncluded(index, included) },
                        onUpdatePercentage = { percent ->
                            onUpdateMemberPercentage(
                                index, percent
                            )
                        },
                        onUpdateAmount = { amount -> onUpdateMemberAmount(index, amount) },
                        totalAmount = amount
                    )
                }

                SplitValidation(
                    splitType = splitType,
                    groupMembers = groupMembers,
                    amount = amount,
                    isPercentageValid = isPercentageValid,
                    isManualAmountValid = isManualAmountValid
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SplitTypeSelector(
    splitType: SplitType, onSplitTypeChange: (SplitType) -> Unit
) {
    val view = LocalView.current
    val options = listOf("Equal", "Percentage", "Manual")
    val unCheckedIcons = listOf(Icons.Outlined.SyncAlt, Icons.Outlined.Percent, Icons.Outlined.Edit)
    val checkedIcons = listOf(Icons.Rounded.SyncAlt, Icons.Rounded.Percent, Icons.Rounded.Edit)
    var selectedIndex by remember { mutableIntStateOf(splitType.ordinal) }

    LaunchedEffect(selectedIndex) {
        onSplitTypeChange(SplitType.entries[selectedIndex])
    }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
    ) {
        val modifiers = listOf(Modifier.weight(1f), Modifier.weight(1.5f), Modifier.weight(1f))

        val customShapes = ToggleButtonShapes(
            shape = RoundedCornerShape(commonCornerRadius),
            pressedShape = RoundedCornerShape(basePadding),
            checkedShape = RoundedCornerShape(basePadding)
        )

        options.forEachIndexed { index, label ->
            ToggleButton(
                checked = selectedIndex == index,
                onCheckedChange = { checked ->
                    if (checked) {
                        selectedIndex = index
                        view.toggleFeedback()
                    }
                },
                modifier = modifiers[index],
                colors = ToggleButtonDefaults.toggleButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    checkedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    checkedContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                shapes = customShapes,
                border = BorderStroke(
                    width = borderStrokeWidth,
                    color = if (selectedIndex == index) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Column(
                    modifier = Modifier.height(basePadding * 3.5f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (selectedIndex == index) checkedIcons[index] else unCheckedIcons[index],
                        contentDescription = label
                    )

                    Spacer(modifier = Modifier.height(extraSmallPadding))
                    Text(
                        text = label, style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SplitMembersHeader(splitType: SplitType) {
    Row(
        modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Member",
            modifier = Modifier.weight(if (splitType == SplitType.PERCENTAGE) 0.3f else 0.4f),
            style = MaterialTheme.typography.labelMediumEmphasized
        )

        Text(
            text = when (splitType) {
                SplitType.EQUAL -> "Amount"
                SplitType.PERCENTAGE -> "Percentage"
                SplitType.CUSTOM -> "Amount"
            },
            modifier = Modifier.weight(if (splitType == SplitType.PERCENTAGE) 0.25f else 0.4f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMediumEmphasized
        )

        if (splitType == SplitType.PERCENTAGE) {
            Text(
                text = "Value",
                modifier = Modifier.weight(0.25f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMediumEmphasized
            )
        }

        Text(
            text = "Include",
            modifier = Modifier.weight(0.2f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMediumEmphasized
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MemberRow(
    member: GroupMember,
    index: Int,
    splitType: SplitType,
    paidBy: String,
    onToggleIncluded: (Boolean) -> Unit,
    onUpdatePercentage: (Int) -> Unit,
    onUpdateAmount: (Double) -> Unit,
    totalAmount: String = "0"
) {
    val isPayer = member.name == paidBy
    val memberNameText = member.name + if (isPayer) " (Payer)" else ""

    Row(
        modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedStrikethroughText(
            text = memberNameText,
            modifier = Modifier
                .weight(if (splitType == SplitType.PERCENTAGE) 0.3f else 0.4f)
                .alpha(if (member.included) 1f else 0.5f),
            textStyle = MaterialTheme.typography.bodyMediumEmphasized,
            isVisible = !member.included
        )

        when (splitType) {
            SplitType.EQUAL -> {
                Box(
                    modifier = Modifier
                        .weight(if (splitType == SplitType.PERCENTAGE) 0.25f else 0.4f)
                        .alpha(if (member.included) 1f else 0.5f),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedStrikethroughText(
                        text = "${
                            String.format(
                                "%.2f", member.amount
                            )
                        }",
                        textStyle = MaterialTheme.typography.labelLargeEmphasized,
                        isVisible = !member.included
                    )
                }
            }

            SplitType.PERCENTAGE -> {
                IFilledSmallerTextField(
                    value = if (member.included) "${member.percentage}%" else "-",
                    onValueChange = { newValue ->
                        val numericValue = newValue.replace("%", "").toIntOrNull()
                        if (numericValue != null) {
                            onUpdatePercentage(numericValue)
                        }
                    },
                    modifier = Modifier
                        .weight(0.25f)
                        .alpha(if (member.included) 1f else 0.5f),
                    placeholder = "",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = member.included
                )
            }

            SplitType.CUSTOM -> {
                IFilledSmallerTextField(
                    value = if (member.included) String.format("%.2f", member.amount) else "-",
                    onValueChange = { newValue ->
                        val numericValue = newValue.toDoubleOrNull()
                        if (numericValue != null) {
                            onUpdateAmount(numericValue)
                        }
                    },
                    modifier = Modifier
                        .weight(if (splitType == SplitType.PERCENTAGE) 0.25f else 0.4f)
                        .padding(horizontal = 8.dp)
                        .alpha(if (member.included) 1f else 0.5f),
                    placeholder = "",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    enabled = member.included
                )
            }
        }

        if (splitType == SplitType.PERCENTAGE) {
            val totalAmountDouble = totalAmount.toDoubleOrNull() ?: 0.0
            val calculatedValue = if (member.included) {
                (member.percentage.toDouble() / 100) * totalAmountDouble
            } else {
                0.0
            }

            AnimatedStrikethroughText(
                text = if (member.included) "$${String.format("%.2f", calculatedValue)}" else "-",
                modifier = Modifier
                    .weight(0.25f)
                    .alpha(if (member.included) 1f else 0.5f),
                textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                isVisible = !member.included
            )
        }

        Checkbox(
            checked = member.included,
            onCheckedChange = onToggleIncluded,
            modifier = Modifier
                .weight(0.2f)
                .semantics {
                    contentDescription = "Include ${member.name}"
                })
    }
}

@Composable
fun SplitValidation(
    splitType: SplitType,
    groupMembers: List<GroupMember>,
    amount: String,
    isPercentageValid: Boolean = false,
    isManualAmountValid: Boolean = false
) {
    when (splitType) {
        SplitType.PERCENTAGE -> {
            val totalPercentage = groupMembers.sumOf { if (it.included) it.percentage else 0 }
            Text(
                text = "Total: $totalPercentage%",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.End,
                color = if (isPercentageValid) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )

            if (!isPercentageValid) {
                Text(
                    text = "Percentages must add up to 100%",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        SplitType.CUSTOM -> {
            val expenseAmount = amount.toDoubleOrNull() ?: 0.0
            val totalManual = groupMembers.sumOf { if (it.included) it.amount else 0.0 }
            val isValid = abs(totalManual - expenseAmount) < 0.01

            Text(
                text = "Expense: $ ${
                    String.format(
                        "%.2f", expenseAmount
                    )
                }, Allocated: $ ${String.format("%.2f", totalManual)}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.End,
                color = if (isManualAmountValid) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )

            if (!isManualAmountValid) {
                Text(
                    text = "Allocation must match total expense amount",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        else -> {/* No validation needed for equal split */
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NotesSection(
    notes: String, onNotesChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = basePadding, vertical = 0.dp)
    ) {
        Text(
            text = "ADDITIONAL NOTES",
            style = MaterialTheme.typography.labelLargeEmphasized,
            color = MaterialTheme.colorScheme.outline
        )

        ITextField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = largePadding * 2),
            value = notes,
            onValueChange = onNotesChange,
            label = "Notes (Optional)",
            placeholder = "Add any additional details about this expense..."
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDismiss: () -> Unit, onDateSelected: (String) -> Unit
) {
    val dateFormatter = DateTimeFormatter.ISO_DATE

    // Create a date picker state initialized with the current date
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        initialDisplayMode = DisplayMode.Picker
    )

    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismiss, confirmButton = {
        TextButton(
            onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val localDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    onDateSelected(localDate.format(dateFormatter))
                }
                onDismiss()
            }, enabled = datePickerState.selectedDateMillis != null
        ) {
            Text("Select")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    }, tonalElevation = 0.dp
    ) {
        DatePicker(
            state = datePickerState,
            showModeToggle = true,
            title = { Text("Select Date", style = MaterialTheme.typography.titleMedium) },
            headline = { /* No headline needed */ })
    }
}

@DevicePreview
@Composable
private fun AddExpenseScreenPreview() {
    var extraInfoState by androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf(
            ""
        )
    }
    PreviewWrapper {
        AddExpenseScreen(
            navController = rememberNavController(),
            expenseState = ExpenseDetailsViewModel.ExpenseState(
                notes = null, extraInfo = extraInfoState
            ),
            formUiState = ExpenseDetailsViewModel.UIState(),
            splitType = SplitType.EQUAL,
            groupMembers = listOf(),
            persons = listOf(),
            snackbarHostState = remember { SnackbarHostState() },
            onExpenseNameChange = {},
            onAmountChange = {},
            onShowDatePicker = {},
            onCategoryChange = {},
            onShowCategoryDropdownChange = {},
            onPaidByChange = {},
            onPaymentMethodChange = {},
            onSplitTypeChange = {},
            onToggleMemberIncluded = { _, _ -> },
            onUpdateMemberPercentage = { _, _ -> },
            onUpdateMemberAmount = { _, _ -> },
            onNotesChange = {},
            onExtraInfoChange = { extraInfoState = it },
            onSaveExpense = {},
            onClearErrorMessage = {},
            onClearSuccessMessage = {},
            onDateSelected = {},
            isPercentageValid = false,
            isManualAmountValid = false
        )
    }
}

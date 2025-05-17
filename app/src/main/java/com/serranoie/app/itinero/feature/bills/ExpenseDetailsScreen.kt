package com.serranoie.app.itinero.feature.bills

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material.icons.outlined.SyncAlt
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Percent
import androidx.compose.material.icons.rounded.SyncAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystem.ui.PreviewWrapper
import com.serranoie.app.designsystem.ui.ThemePreviews
import com.serranoie.app.designsystem.ui.theme.component.ITextField
import com.serranoie.app.designsystem.ui.theme.component.card.ButtonImportance
import com.serranoie.app.designsystem.ui.theme.component.card.IButton
import com.serranoie.app.itinero.feature.bills.components.SelectField
import com.serranoie.app.itinero.utils.ExpenseCategory
import com.serranoie.app.itinero.utils.icon
import java.time.format.DateTimeFormatter
import kotlin.math.abs

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailsScreen(
    navController: NavController,
    viewModel: ExpenseDetailsViewModel = viewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val expenseState by viewModel.expenseState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val splitType by viewModel.splitType.collectAsStateWithLifecycle()
    val groupMembers by viewModel.groupMembers.collectAsStateWithLifecycle()

    if (uiState.showSuccessMessage) {
        LaunchedEffect(Unit) {
            // Navigate or show snackbar
        }
    }

    if (uiState.showDatePicker) {
        DatePickerDialog(
            onDismiss = { viewModel.toggleDatePicker(false) },
            onDateSelected = { viewModel.updateDate(it) })
    }

    // Error dialog
    if (uiState.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { },
            icon = { Icon(Icons.Default.Error, contentDescription = null) },
            title = { Text("Error") },
            text = { Text(uiState.errorMessage!!) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearErrorMessage()
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
                            text = if (expenseState.name.isNotEmpty()) expenseState.name else "New Expense",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.headlineMediumEmphasized
                        )
                        if (expenseState.amount.isNotEmpty()) {
                            Text(
                                text = "${expenseState.currency} ${expenseState.amount}",
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
                    Button(
                        onClick = { viewModel.saveExpense() },
                        enabled = !expenseState.isSaving,
                        modifier = Modifier.heightIn(ButtonDefaults.ExtraSmallContainerHeight),
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                ExpenseBasicDetailsSection(
                    expenseName = expenseState.name,
                    expenseNameError = expenseState.nameError,
                    onExpenseNameChange = viewModel::updateExpenseName,
                    amount = expenseState.amount,
                    amountError = expenseState.amountError,
                    onAmountChange = viewModel::updateAmount,
                    date = expenseState.date,
                    onDateChange = viewModel::updateDate,
                    onShowDatePicker = { viewModel.toggleDatePicker(true) },
                    category = expenseState.category,
                    onCategoryChange = viewModel::updateCategory,
                    currency = expenseState.currency,
                    onCurrencyChange = viewModel::updateCurrency,
                    currencies = viewModel.currencies,
                    showCategoryDropdown = uiState.showCategoryDropdown,
                    onShowCategoryDropdownChange = viewModel::toggleCategoryDropdown,
                    showCurrencyDropdown = uiState.showCurrencyDropdown,
                    onShowCurrencyDropdownChange = viewModel::toggleCurrencyDropdown
                )
            }

            // Payment section
            item {
                PaymentDetailsSection(
                    paidBy = expenseState.paidBy,
                    onPaidByChange = viewModel::updatePaidBy,
                    persons = viewModel.persons,
                    showPersonsDropdown = uiState.showPersonsDropdown,
                    paymentMethod = expenseState.paymentMethod,
                    onPaymentMethodChange = viewModel::updatePaymentMethod,
                    paymentMethods = viewModel.paymentMethods,
                    showPaymentMethodDropdown = uiState.showPaymentMethodDropdown,
                    onShowPaymentMethodDropdownChange = viewModel::togglePaymentMethodDropdown,
                    onShowPersonsDropdownChange = viewModel::togglePersonsDropdown
                )
            }

            // Split section
            item {
                SplitDetailsSection(
                    splitType = splitType,
                    onSplitTypeChange = viewModel::updateSplitType,
                    groupMembers = groupMembers.toMutableStateList(),
                    onToggleMemberIncluded = viewModel::toggleMemberIncluded,
                    onUpdateMemberPercentage = viewModel::updateMemberPercentage,
                    onUpdateMemberAmount = viewModel::updateMemberAmount,
                    onAddMember = viewModel::addMember,
                    amount = expenseState.amount,
                    paidBy = expenseState.paidBy,
                    isPercentageValid = viewModel.isPercentageValid(),
                    isManualAmountValid = viewModel.isManualAmountValid()
                )
            }

            // Notes section
            item {
                NotesSection(
                    notes = expenseState.notes, onNotesChange = viewModel::updateNotes
                )
            }
        }
    }
}

enum class SplitType { EQUAL, PERCENTAGE, MANUAL }

data class GroupMember(
    val name: String, var included: Boolean, val percentage: Int = 0, val amount: Double = 0.0
)

// Helper to convert list to mutable state list for UI
inline fun <reified T> List<T>.toMutableStateList() =
    mutableStateListOf(*this.toMutableList().toTypedArray())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseBasicDetailsSection(
    expenseName: String,
    expenseNameError: String?,
    onExpenseNameChange: (String) -> Unit,
    amount: String,
    amountError: String?,
    onAmountChange: (String) -> Unit,
    date: String,
    onDateChange: (String) -> Unit,
    onShowDatePicker: () -> Unit,
    category: ExpenseCategory,
    onCategoryChange: (ExpenseCategory) -> Unit,
    currency: String,
    onCurrencyChange: (String) -> Unit,
    currencies: List<String>,
    showCategoryDropdown: Boolean,
    onShowCategoryDropdownChange: (Boolean) -> Unit,
    showCurrencyDropdown: Boolean,
    onShowCurrencyDropdownChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
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

        Box(modifier = Modifier.fillMaxWidth()) {
            ITextField(
                modifier = Modifier.fillMaxWidth(),
                value = amount,
                onValueChange = onAmountChange,
                label = "Amount",
                placeholder = "",
                leadingIcon = Icons.Default.CurrencyExchange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp, top = 8.dp)
                    .clickable { onShowCurrencyDropdownChange(true) }) {
                Text(currency)
                DropdownMenu(
                    expanded = showCurrencyDropdown,
                    onDismissRequest = { onShowCurrencyDropdownChange(false) }) {
                    currencies.forEach { curr ->
                        DropdownMenuItem(text = { Text(curr) }, onClick = {
                            onCurrencyChange(curr)
                            onShowCurrencyDropdownChange(false)
                        })
                    }
                }
            }
        }
        amountError?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Date field using SelectField
        SelectField(
            value = date,
            onSelect = { onShowDatePicker() },
            label = "Date",
            leadingIcon = Icons.Default.CalendarToday,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category field using SelectField
        Box(modifier = Modifier.fillMaxWidth()) {
            SelectField(
                value = category.displayName,
                onSelect = { onShowCategoryDropdownChange(true) },
                label = "Category",
                leadingIcon = category.icon(),
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenu(
                expanded = showCategoryDropdown,
                onDismissRequest = { onShowCategoryDropdownChange(false) }
            ) {
                ExpenseCategory.values().sortedBy { it.displayName }.forEach { cat ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    cat.icon(),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = LocalContentColor.current
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(cat.displayName)
                            }
                        },
                        onClick = {
                            onCategoryChange(cat)
                            onShowCategoryDropdownChange(false)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PaymentDetailsSection(
    paidBy: String,
    onPaidByChange: (String) -> Unit,
    persons: List<String>,
    showPersonsDropdown: Boolean,
    paymentMethod: String,
    onPaymentMethodChange: (String) -> Unit,
    paymentMethods: List<String>,
    showPaymentMethodDropdown: Boolean,
    onShowPaymentMethodDropdownChange: (Boolean) -> Unit,
    onShowPersonsDropdownChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "PAYMENT DETAILS",
            style = MaterialTheme.typography.labelLargeEmphasized,
            color = MaterialTheme.colorScheme.primary
        )

        // Paid By section
        Box(modifier = Modifier.fillMaxWidth()) {
            SelectField(
                value = paidBy,
                onSelect = { onShowPersonsDropdownChange(true) },
                label = "Paid By",
                leadingIcon = Icons.Default.Person,
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenu(
                expanded = showPersonsDropdown,
                onDismissRequest = { onShowPersonsDropdownChange(false) }
            ) {
                persons.forEach { person ->
                    DropdownMenuItem(text = { Text(person) }, onClick = {
                        onPaidByChange(person)
                        onShowPersonsDropdownChange(false)
                    })
                }
            }
        }

        // Payment Method section
        Box(modifier = Modifier.fillMaxWidth()) {
            SelectField(
                value = paymentMethod,
                onSelect = { onShowPaymentMethodDropdownChange(true) },
                label = "Payment Method",
                leadingIcon = Icons.Default.Payment,
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenu(
                expanded = showPaymentMethodDropdown,
                onDismissRequest = { onShowPaymentMethodDropdownChange(false) }) {
                paymentMethods.forEach { method ->
                    DropdownMenuItem(text = { Text(method) }, onClick = {
                        onPaymentMethodChange(method)
                        onShowPaymentMethodDropdownChange(false)
                    })
                }
            }
        }

        IButton(
            onClick = { /* In a real app, launch camera intent */ },
            modifier = Modifier.fillMaxWidth(),
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.PhotoCamera,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = LocalContentColor.current
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Receipt")
                }
            },
            importance = ButtonImportance.Secondary
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SplitDetailsSection(
    splitType: SplitType,
    onSplitTypeChange: (SplitType) -> Unit,
    groupMembers: SnapshotStateList<GroupMember>,
    onToggleMemberIncluded: (Int, Boolean) -> Unit,
    onUpdateMemberPercentage: (Int, Int) -> Unit,
    onUpdateMemberAmount: (Int, Double) -> Unit,
    onAddMember: (String) -> Unit,
    amount: String,
    paidBy: String,
    isPercentageValid: Boolean,
    isManualAmountValid: Boolean
) {
    Column(
        modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "SPLIT DETAILS",
            style = MaterialTheme.typography.labelLargeEmphasized,
            color = MaterialTheme.colorScheme.primary
        )

        SplitTypeSelector(
            splitType = splitType, onSplitTypeChange = onSplitTypeChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        SplitMembersHeader(splitType = splitType)

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        groupMembers.forEachIndexed { index, member ->
            MemberRow(
                member = member,
                index = index,
                splitType = splitType,
                paidBy = paidBy,
                onToggleIncluded = { included -> onToggleMemberIncluded(index, included) },
                onUpdatePercentage = { percent -> onUpdateMemberPercentage(index, percent) },
                onUpdateAmount = { amount -> onUpdateMemberAmount(index, amount) })
        }

        SplitValidation(
            splitType = splitType,
            groupMembers = groupMembers,
            amount = amount,
            isPercentageValid = isPercentageValid,
            isManualAmountValid = isManualAmountValid
        )

        Spacer(modifier = Modifier.height(16.dp))

        IButton(
            onClick = { onAddMember("New Member") },
            modifier = Modifier.fillMaxWidth(),
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Add Member")
                }
            },
            importance = ButtonImportance.Primary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SplitTypeSelector(
    splitType: SplitType, onSplitTypeChange: (SplitType) -> Unit
) {
    val options = listOf("Equal", "Percentage", "Manual")
    val unCheckedIcons = listOf(Icons.Outlined.SyncAlt, Icons.Outlined.Percent, Icons.Outlined.Edit)
    val checkedIcons = listOf(Icons.Rounded.SyncAlt, Icons.Rounded.Percent, Icons.Rounded.Edit)
    var selectedIndex by remember { mutableIntStateOf(splitType.ordinal) }

    LaunchedEffect(selectedIndex) {
        onSplitTypeChange(SplitType.entries[selectedIndex])
    }

    Row(
        Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
    ) {
        val modifiers = listOf(Modifier.weight(1f), Modifier.weight(1.5f), Modifier.weight(1f))

        options.forEachIndexed { index, label ->
            ToggleButton(
                checked = selectedIndex == index,
                onCheckedChange = { selectedIndex = index },
                modifier = modifiers[index],
                shapes = when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                }
            ) {
                Column(
                    modifier = Modifier
                        .height(56.dp)
                        .animateContentSize(), // Added to smoothly animate size changes
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Crossfade(
                        targetState = selectedIndex == index, label = "icon animation"
                    ) { isSelected ->
                        Icon(
                            if (isSelected) checkedIcons[index] else unCheckedIcons[index],
                            contentDescription = label
                        )
                    }

                    AnimatedVisibility(
                        visible = selectedIndex == index,
                        enter = expandHorizontally(),
                        exit = shrinkHorizontally()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = label, style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SplitMembersHeader(splitType: SplitType) {
    Row(
        modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Member",
            modifier = Modifier.weight(1.5f),
            style = MaterialTheme.typography.titleSmall
        )

        Text(
            text = when (splitType) {
                SplitType.EQUAL -> "Amount"
                SplitType.PERCENTAGE -> "Percentage"
                SplitType.MANUAL -> "Amount"
            },
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall
        )

        Text(
            text = "Include",
            modifier = Modifier.weight(0.5f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
fun MemberRow(
    member: GroupMember,
    index: Int,
    splitType: SplitType,
    paidBy: String,
    onToggleIncluded: (Boolean) -> Unit,
    onUpdatePercentage: (Int) -> Unit,
    onUpdateAmount: (Double) -> Unit
) {
    val isPayer = member.name == paidBy
    val memberNameText = member.name + if (isPayer) " (Payer)" else ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = memberNameText,
            modifier = Modifier
                .weight(1.5f)
                .alpha(if (member.included) 1f else 0.6f),
            style = MaterialTheme.typography.bodyMedium
        )

        when (splitType) {
            SplitType.EQUAL -> {
                Text(
                    text = if (member.included) "€${String.format("%.2f", member.amount)}" else "-",
                    modifier = Modifier
                        .weight(1f)
                        .alpha(if (member.included) 1f else 0.6f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            SplitType.PERCENTAGE -> {
                ITextField(
                    value = if (member.included) "${member.percentage}%" else "-",
                    onValueChange = { newValue ->
                        val numericValue = newValue.replace("%", "").toIntOrNull()
                        if (numericValue != null) {
                            onUpdatePercentage(numericValue)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .alpha(if (member.included) 1f else 0.6f),
                    label = "",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            SplitType.MANUAL -> {
                ITextField(
                    value = if (member.included) String.format("%.2f", member.amount) else "-",
                    onValueChange = { newValue ->
                        val numericValue = newValue.toDoubleOrNull()
                        if (numericValue != null) {
                            onUpdateAmount(numericValue)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .alpha(if (member.included) 1f else 0.6f),
                    label = "",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
        }

        Checkbox(
            checked = member.included,
            onCheckedChange = onToggleIncluded,
            modifier = Modifier
                .weight(0.5f)
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

        SplitType.MANUAL -> {
            val expenseAmount = amount.toDoubleOrNull() ?: 0.0
            val totalManual = groupMembers.sumOf { if (it.included) it.amount else 0.0 }
            val isValid = abs(totalManual - expenseAmount) < 0.01

            Text(
                text = "Expense: €${
                    String.format(
                        "%.2f", expenseAmount
                    )
                }, Allocated: €${String.format("%.2f", totalManual)}",
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

        else -> { /* No validation needed for equal split */
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NotesSection(
    notes: String, onNotesChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "ADDITIONAL NOTES",
            style = MaterialTheme.typography.labelLargeEmphasized,
            color = MaterialTheme.colorScheme.primary
        )

        ITextField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp),
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
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    val dateFormatter = DateTimeFormatter.ISO_DATE

    // Create a date picker state initialized with the current date
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        initialDisplayMode = DisplayMode.Picker
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val localDate = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(localDate.format(dateFormatter))
                    }
                    onDismiss()
                },
                enabled = datePickerState.selectedDateMillis != null
            ) {
                Text("Select")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        tonalElevation = 0.dp
    ) {
        DatePicker(
            state = datePickerState,
            showModeToggle = true,
            title = { Text("Select Date", style = MaterialTheme.typography.titleMedium) },
            headline = { /* No headline needed */ }
        )
    }
}

@ThemePreviews
@Composable
private fun ExpenseDetailsScreenPreview() {
    PreviewWrapper {
        ExpenseDetailsScreen(navController = rememberNavController())
    }
}

data class DropdownItem(
    val icon: ImageVector, val title: String
)
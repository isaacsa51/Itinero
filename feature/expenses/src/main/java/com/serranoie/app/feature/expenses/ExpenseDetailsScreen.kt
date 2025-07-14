package com.serranoie.app.feature.expenses

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.ThemePreviews
import com.serranoie.app.designsystemlib.ui.theme.component.ButtonImportance
import com.serranoie.app.designsystemlib.ui.theme.component.IButton
import com.serranoie.app.designsystemlib.ui.theme.component.ITextButton
import com.serranoie.app.designsystemlib.ui.theme.component.SlideToConfirm
import com.serranoie.app.designsystemlib.ui.theme.component.card.TicketView
import com.serranoie.app.designsystemlib.ui.theme.component.shimmerable
import com.serranoie.app.designsystemlib.ui.utils.Constants.basePadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.mediumPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.smallPadding
import com.serranoie.app.designsystemlib.ui.utils.Utils.formatCurrency
import com.serranoie.app.feature.expenses.domain.model.Expense
import com.serranoie.app.feature.expenses.domain.model.ExpenseDebtor
import com.serranoie.app.feature.expenses.util.ExpenseCategory
import com.serranoie.app.feature.expenses.util.ExpenseSplitType
import com.serranoie.app.feature.expenses.util.icon

@Composable
fun ExpenseDetailsScreen(
    navController: NavController,
    expenseState: ExpenseDetailsViewModel.ExpenseState,
    formUiState: ExpenseDetailsViewModel.UIState,
    splitType: SplitType,
    groupMembers: List<GroupMember>,
    persons: List<String>,
    paymentMethods: List<String>,
    selectedExpense: Expense? = null,
    currentUserId: Int? = null,
    isLoading: Boolean = false,
    onMarkAsPaid: () -> Unit = {},
    onCancelMarkAsPaid: () -> Unit = {},
    onDeleteExpense: () -> Unit = {},
    onEditExpense: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(formUiState.showSuccessMessage) {
        if (formUiState.showSuccessMessage) {
            snackbarHostState.showSnackbar("Operation completed successfully!")
        }
    }

    LaunchedEffect(formUiState.errorMessage) {
        formUiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    if (isLoading) {
        ExpenseDetailSkeleton()
        return
    }

    val isCurrentUserCreator = selectedExpense?.paidByUserId == currentUserId
    val paidByUserId = expenseState.paidByUserId
    val owesItems = groupMembers.filter { it.userId != paidByUserId }.map {
        OwesData(
            name = "${it.name} ${it.surname}", isOwed = it.amount > 0, amount = it.amount
        )
    }

    val expenseSplitType = ExpenseSplitType.fromSplitTypeName(splitType.name)

    val currentUserDebtor = remember {
        derivedStateOf {
            selectedExpense?.debtors?.find { it.userId == currentUserId }
        }
    }

    ExpenseDetailsScreenWithData(
        navController = navController,
        expenseState = expenseState,
        formUiState = formUiState,
        splitType = expenseSplitType,
        groupMembers = groupMembers,
        persons = persons,
        paymentMethods = paymentMethods,
        owesItems = owesItems,
        selectedExpense = selectedExpense,
        isCurrentUserCreator = isCurrentUserCreator,
        currentUserDebtor = currentUserDebtor.value,
        snackbarHostState = snackbarHostState,
        onMarkAsPaid = onMarkAsPaid,
        onCancelMarkAsPaid = onCancelMarkAsPaid,
        onDeleteExpense = onDeleteExpense,
        onEditExpense = onEditExpense
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ExpenseDetailsScreenWithData(
    navController: NavController,
    expenseState: ExpenseDetailsViewModel.ExpenseState,
    formUiState: ExpenseDetailsViewModel.UIState,
    splitType: ExpenseSplitType,
    groupMembers: List<GroupMember>,
    persons: List<String>,
    paymentMethods: List<String>,
    owesItems: List<OwesData>,
    selectedExpense: Expense?,
    isCurrentUserCreator: Boolean,
    currentUserDebtor: ExpenseDebtor?,
    snackbarHostState: SnackbarHostState,
    onMarkAsPaid: () -> Unit,
    onCancelMarkAsPaid: () -> Unit,
    onDeleteExpense: () -> Unit,
    onEditExpense: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val scrollState = scrollBehavior.state
    val appBarExpanded by remember {
        derivedStateOf { scrollState.collapsedFraction < 0.9f }
    }
    val expandedAppBarHeight = 180.dp
    val headerTranslation = (expandedAppBarHeight / 2)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                CollapsedExpenseHeader(
                    navController = navController,
                    modifier = Modifier,
                    visible = !appBarExpanded,
                    expenseState = expenseState,
                    isCurrentUserCreator = isCurrentUserCreator,
                    onEditExpense = onEditExpense
                )
                TopAppBar(
                    title = {
                        ExpandedHeader(
                            modifier = Modifier.graphicsLayer {
                                translationY =
                                    scrollState.collapsedFraction * headerTranslation.toPx()
                            }, visible = appBarExpanded, expenseState = expenseState
                        )
                    }, colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    ), scrollBehavior = scrollBehavior
                )
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = smallPadding)
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(top = basePadding)
        ) {
            item {
                ExpenseDetailsCard(
                    paidBy = expenseState.paidBy ?: "Unknown",
                    splitType = splitType,
                    category = expenseState.category,
                    owesItems = owesItems,
                    notes = expenseState.notes,
                    date = expenseState.date,
                    paymentMethod = expenseState.paymentMethod,
                    isCurrentUserCreator = isCurrentUserCreator,
                    currentUserDebtor = currentUserDebtor,
                    formUiState = formUiState,
                    onMarkAsPaid = onMarkAsPaid,
                    onCancelMarkAsPaid = onCancelMarkAsPaid,
                    onDeleteExpense = onDeleteExpense
                )
                Spacer(modifier = Modifier.height(basePadding))
            }
        }
    }
}

@Composable
private fun ExpenseDetailsCard(
    paidBy: String,
    splitType: ExpenseSplitType,
    category: ExpenseCategory,
    owesItems: List<OwesData>,
    notes: String?,
    date: String,
    paymentMethod: String,
    isCurrentUserCreator: Boolean,
    currentUserDebtor: ExpenseDebtor?,
    formUiState: ExpenseDetailsViewModel.UIState,
    onMarkAsPaid: () -> Unit,
    onCancelMarkAsPaid: () -> Unit,
    onDeleteExpense: () -> Unit
) {
    TicketView(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            PaidBySection(paidBy = paidBy, isCurrentUserCreator = isCurrentUserCreator)

            HorizontalDivider(
                modifier = Modifier.padding(
                    horizontal = smallPadding, vertical = basePadding
                )
            )

            ExpenseInfoHeader(
                date = date, paymentMethod = paymentMethod
            )

            AdditionalInfo(splitType = splitType, category = category)

            HorizontalDivider(
                modifier = Modifier.padding(
                    horizontal = smallPadding, vertical = basePadding
                )
            )

            OwesSection(owesItems = owesItems)

            HorizontalDivider(
                modifier = Modifier.padding(
                    horizontal = smallPadding, vertical = basePadding
                )
            )

            RemainingToPaySection(owesItems = owesItems)

            HorizontalDivider(
                modifier = Modifier.padding(
                    horizontal = smallPadding, vertical = basePadding
                )
            )

            NotesSection(notes = notes)

            Spacer(modifier = Modifier.height(basePadding))

            if (isCurrentUserCreator) {
                IButton(
                    onClick = onDeleteExpense,
                    text = { Text("Delete Expense") },
                    importance = ButtonImportance.Error,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = smallPadding)
                        .padding(bottom = mediumPadding)
                )
            } else {
                Column(
                    modifier = Modifier.padding(horizontal = smallPadding)
                ) {
                    // Show user's share amount if available
                    currentUserDebtor?.let { debtor ->
                        Text(
                            text = "Your share: ${formatCurrency(debtor.amount.toString())}",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    SlideToConfirm(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = mediumPadding),
                        isLoading = formUiState.isMarkingAsPaid,
                        currentStatus = formUiState.currentUserDebtorStatus,
                        onAcceptSwipe = onMarkAsPaid,
                        onCancelPressed = onCancelMarkAsPaid,
                        hint = if (formUiState.currentUserDebtorStatus)
                            "Already marked as paid"
                        else
                            "Swipe to mark as paid"
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ExpenseInfoHeader(
    date: String, paymentMethod: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(vertical = basePadding)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.shimmerable(),
                text = "Expense creation:", style = MaterialTheme.typography.titleMediumEmphasized
            )
            Text(
                modifier = Modifier.shimmerable(),
                text = date, style = MaterialTheme.typography.titleMedium
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.shimmerable(),
                text = "Paid using:", style = MaterialTheme.typography.titleMediumEmphasized
            )
            Text(
                modifier = Modifier.shimmerable(),
                text = paymentMethod, style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun RemainingToPaySection(owesItems: List<OwesData>) {
    val remainingAmount = owesItems.filter { it.isOwed }.sumOf { it.amount }

    if (remainingAmount > 0) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Remaining to pay:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.shimmerable()
                )

                Text(
                    text = formatCurrency(remainingAmount.toString()),
                    style = MaterialTheme.typography.bodyLargeEmphasized,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.shimmerable()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PaidBySection(paidBy: String, isCurrentUserCreator: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = basePadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.shimmerable(),
            text = "Paid by",
            style = MaterialTheme.typography.headlineSmallEmphasized
        )

        // Only show edit button if user is the creator
        if (isCurrentUserCreator) {
            IconButton(
                modifier = Modifier.shimmerable(),
                onClick = { /* Implement edit action */ }
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit Paid by",
                )
            }
        }
    }
    Text(
        modifier = Modifier.shimmerable(),
        text = paidBy,
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
private fun AdditionalInfo(
    splitType: ExpenseSplitType, category: ExpenseCategory
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()
    ) {
        MetadataItem(
            title = "Split type",
            icon = splitType.icon(),
            label = splitType.displayName,
            iconTint = MaterialTheme.colorScheme.onSurface
        )
        MetadataItem(
            title = "Category",
            icon = category.icon(),
            label = category.displayName,
            iconTint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MetadataItem(
    title: String,
    icon: ImageVector,
    label: String,
    iconTint: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.shimmerable(),
            text = title, style = MaterialTheme.typography.labelMediumEmphasized
        )
        Text(
            modifier = Modifier.shimmerable(),
            text = label, style = MaterialTheme.typography.bodyMedium
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun OwesSection(owesItems: List<OwesData>) {
    Text(
        modifier = Modifier.shimmerable(),
        text = "Who owes", style = MaterialTheme.typography.headlineSmallEmphasized
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = basePadding)
    ) {
        owesItems.forEach { item ->
            OwesItem(
                payerName = item.name, isOwed = item.isOwed, amount = item.amount
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun NotesSection(notes: String?) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Extra information",
            style = MaterialTheme.typography.titleMediumEmphasized,
            modifier = Modifier.shimmerable()
        )
        Text(
            text = notes ?: "No extra information/notes given for this expense",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.shimmerable()
        )
        Spacer(modifier = Modifier.height(basePadding))
        ITextButton(
            onClick = {},
            height = 32.dp,
            text = {
                Text(
                    "See attached receipt", style = MaterialTheme.typography.labelMediumEmphasized
                )
            },
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OwesItem(
    payerName: String, isOwed: Boolean, amount: Double
) {
    Column(Modifier.padding(vertical = 8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = payerName,
                style = MaterialTheme.typography.bodyMediumEmphasized,
                textDecoration = if (isOwed) TextDecoration.None else TextDecoration.LineThrough,
                modifier = Modifier
                    .weight(0.4f)
                    .shimmerable()
            )

            if (isOwed) {
                Text(
                    text = "Owes",
                    style = MaterialTheme.typography.labelMediumEmphasized,
                    modifier = Modifier
                        .weight(0.2f)
                        .shimmerable(),
                    textAlign = TextAlign.Center
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Edit Paid by",
                    modifier = Modifier
                        .weight(0.2f)
                        .height(18.dp)
                )
            }

            Text(
                text = formatCurrency(amount.toString()),
                style = MaterialTheme.typography.bodyMediumEmphasized,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .weight(0.25f)
                    .shimmerable(),
                textDecoration = if (isOwed) TextDecoration.None else TextDecoration.LineThrough
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CollapsedExpenseHeader(
    navController: NavController,
    modifier: Modifier,
    visible: Boolean,
    expenseState: ExpenseDetailsViewModel.ExpenseState,
    isCurrentUserCreator: Boolean,
    onEditExpense: () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back"
                )
            }
        },
        title = {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween()),
                exit = fadeOut(animationSpec = tween())
            ) {
                Column {
                    Text(
                        text = expenseState.name,
                        style = MaterialTheme.typography.titleLargeEmphasized
                    )
                    Text(
                        text = formatCurrency(expenseState.amount),
                        style = MaterialTheme.typography.labelLargeEmphasized,
                    )
                }
            }
        },
        actions = {
            if (isCurrentUserCreator) {
                IconButton(onClick = onEditExpense) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit expense"
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpandedHeader(
    modifier: Modifier = Modifier,
    expenseState: ExpenseDetailsViewModel.ExpenseState,
    visible: Boolean
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween()),
        exit = fadeOut(animationSpec = tween())
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(basePadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        modifier = modifier.shimmerable(),
                        text = expenseState.name,
                        style = MaterialTheme.typography.displaySmallEmphasized
                    )

                    Text(
                        modifier = modifier.shimmerable(),
                        text = "${formatCurrency(expenseState.amount)}",
                        style = MaterialTheme.typography.displaySmallEmphasized,
                    )
                }
            }
        }
    }
}

@Composable
fun CutoutTicketDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    circleRadius: Dp = 14.dp,
    rectWidth: Dp = 12.dp,
    rectHeight: Dp = 3.dp,
    cornerRadius: Dp = 1.dp,
    spacingWidth: Dp = basePadding
) {
    val density = LocalDensity.current
    val cutoutColor = MaterialTheme.colorScheme.surface

    val dotRadiusPx = with(density) { circleRadius.toPx() }
    val rectWidthPx = with(density) { rectWidth.toPx() }
    val rectHeightPx = with(density) { rectHeight.toPx() }
    val spacingPx = with(density) { spacingWidth.toPx() }
    val cornerRadiusPx = with(density) { cornerRadius.toPx() }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp + 2 * circleRadius)
    ) {
        val leftBound = dotRadiusPx
        val rightBound = size.width - dotRadiusPx
        val availableWidth = rightBound - leftBound

        val totalUnit = rectWidthPx + spacingPx
        val rectCount = ((availableWidth + spacingPx) / totalUnit).toInt()

        val usedWidth = rectCount * totalUnit - spacingPx
        val startX = leftBound + (availableWidth - usedWidth) / 2 + rectWidthPx / 2

        repeat(rectCount) { i ->
            val x = startX + i * totalUnit
            drawRoundRect(
                color = color,
                topLeft = Offset(x - rectWidthPx / 2, size.height / 2 - rectHeightPx / 2),
                size = Size(rectWidthPx, rectHeightPx),
                cornerRadius = CornerRadius(cornerRadiusPx)
            )
        }
        drawArc(
            color = cutoutColor,
            startAngle = 270f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(-dotRadiusPx, size.height / 2 - dotRadiusPx),
            size = Size(dotRadiusPx * 2, dotRadiusPx * 2)
        )

        drawArc(
            color = cutoutColor,
            startAngle = 90f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(size.width - dotRadiusPx, size.height / 2 - dotRadiusPx),
            size = Size(dotRadiusPx * 2, dotRadiusPx * 2)
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpenseDetailSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(basePadding)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Loading expense...",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.shimmerable()
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "$ 000.00",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.shimmerable()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        TicketView(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = basePadding),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Paid by",
                        style = MaterialTheme.typography.headlineSmallEmphasized,
                        modifier = Modifier.shimmerable()
                    )
                    IconButton(
                        onClick = { },
                        modifier = Modifier.shimmerable()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit Paid by",
                        )
                    }
                }
                Text(
                    text = "Loading user...",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.shimmerable()
                )
                
                HorizontalDivider(
                    modifier = Modifier.padding(
                        horizontal = smallPadding, vertical = basePadding
                    )
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(vertical = basePadding)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Expense creation:",
                            style = MaterialTheme.typography.titleMediumEmphasized,
                            modifier = Modifier.shimmerable()
                        )
                        Text(
                            text = "Loading date...",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.shimmerable()
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Paid using:",
                            style = MaterialTheme.typography.titleMediumEmphasized,
                            modifier = Modifier.shimmerable()
                        )
                        Text(
                            text = "Loading method...",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.shimmerable()
                        )
                    }
                }
                
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Split type",
                            style = MaterialTheme.typography.labelMediumEmphasized,
                            modifier = Modifier.shimmerable()
                        )
                        Text(
                            text = "Loading...",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.shimmerable()
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Category",
                            style = MaterialTheme.typography.labelMediumEmphasized,
                            modifier = Modifier.shimmerable()
                        )
                        Text(
                            text = "Loading...",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.shimmerable()
                        )
                    }
                }
                
                HorizontalDivider(
                    modifier = Modifier.padding(
                        horizontal = smallPadding, vertical = basePadding
                    )
                )
                
                Text(
                    text = "Who owes",
                    style = MaterialTheme.typography.headlineSmallEmphasized,
                    modifier = Modifier.shimmerable()
                )
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = basePadding)
                ) {
                    repeat(3) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Loading user...",
                                style = MaterialTheme.typography.bodyMediumEmphasized,
                                modifier = Modifier
                                    .weight(0.4f)
                                    .shimmerable()
                            )
                            Text(
                                text = "Owes",
                                style = MaterialTheme.typography.labelMediumEmphasized,
                                modifier = Modifier
                                    .weight(0.2f)
                                    .shimmerable(),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "$ 000.00",
                                style = MaterialTheme.typography.bodyMediumEmphasized,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                                modifier = Modifier
                                    .weight(0.25f)
                                    .shimmerable()
                            )
                        }
                    }
                }
                
                HorizontalDivider(
                    modifier = Modifier.padding(
                        horizontal = smallPadding, vertical = basePadding
                    )
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Remaining to pay:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.shimmerable()
                    )
                    Text(
                        text = "$ 000.00",
                        style = MaterialTheme.typography.bodyLargeEmphasized,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.shimmerable()
                    )
                }
                
                HorizontalDivider(
                    modifier = Modifier.padding(
                        horizontal = smallPadding, vertical = basePadding
                    )
                )
                
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Extra information",
                        style = MaterialTheme.typography.titleMediumEmphasized,
                        modifier = Modifier.shimmerable()
                    )
                    Text(
                        text = "Loading notes...",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.shimmerable()
                    )
                    Spacer(modifier = Modifier.height(basePadding))
                    ITextButton(
                        onClick = {},
                        height = 32.dp,
                        text = {
                            Text(
                                "See attached receipt",
                                style = MaterialTheme.typography.labelMediumEmphasized
                            )
                        },
                    )
                }
                
                Spacer(modifier = Modifier.height(basePadding))
                
                SlideToConfirm(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = smallPadding)
                        .padding(bottom = mediumPadding)
                        .shimmerable(),
                    isLoading = false,
                    onAcceptSwipe = { },
                    currentStatus = false,
                    onCancelPressed = { },
                    hint = "Loading..."
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun SkeletonPreview() {
    PreviewWrapper {
        ExpenseDetailSkeleton()
    }
}

@ThemePreviews
@Composable
private fun ExpenseDetailsScreenPreview() {
    PreviewWrapper {
        ExpenseDetailsScreen(
            navController = rememberNavController(),
            expenseState = ExpenseDetailsViewModel.ExpenseState(
                name = "Dinner at Tokyo",
                amount = "450.00",
                date = "2023-10-15",
                category = ExpenseCategory.FOOD,
                paidByUserId = 1,
                paymentMethod = "Debit Card",
                notes = null
            ),
            formUiState = ExpenseDetailsViewModel.UIState(),
            splitType = SplitType.PERCENTAGE,
            groupMembers = listOf(
                GroupMember(
                    userId = 0,
                    name = "John",
                    surname = "Smith",
                    amount = 150.00,
                    included = true,
                    percentage = 35
                ), GroupMember(
                    userId = 1,
                    name = "Alice",
                    surname = "Jones",
                    amount = 200.00,
                    included = true,
                    percentage = 45
                ), GroupMember(
                    userId = 2,
                    name = "Charlie",
                    surname = "Brown",
                    amount = 100.00,
                    included = true,
                    percentage = 20
                ), GroupMember(
                    userId = 3,
                    name = "Bob",
                    surname = "Johnson",
                    amount = 0.00,
                    included = false,
                    percentage = 0
                )
            ),
            persons = listOf("John Smith", "Alice Jones", "Charlie Brown"),
            paymentMethods = listOf("Cash", "Debit Card", "Credit Card"),
            currentUserId = 2
        )
    }
}

data class OwesData(
    val name: String, val isOwed: Boolean, val amount: Double
)

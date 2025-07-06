package com.serranoie.app.feature.expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.ThemePreviews
import com.serranoie.app.designsystemlib.ui.theme.component.DateRangeToolbar
import com.serranoie.app.designsystemlib.ui.theme.component.ShimmerProvider
import com.serranoie.app.designsystemlib.ui.theme.component.card.ExpenseCard
import com.serranoie.app.designsystemlib.ui.theme.component.shimmerable
import com.serranoie.app.feature.expenses.domain.model.Expense
import com.serranoie.app.feature.expenses.domain.model.UserExpenseSummary
import com.serranoie.app.feature.expenses.util.generateDateRange
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpensesScreen(
    navController: NavController,
    tripId: String,
    uiState: ExpensesUiState,
    expenses: List<UserExpenseSummary>,
    onRefresh: () -> Unit,
    onSwiped: () -> Unit,
    onExpenseClick: () -> Unit,
    onAddExpenseClick: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val isLoading = uiState is ExpensesUiState.Loading
    val allExpenses = expenses.flatMap { it.expenses }
    val balanceData = calculateBalanceData(expenses)
    val expensesByDate = groupExpensesByDate(allExpenses)
    val dateRange = if (allExpenses.isNotEmpty()) {
        val dates = allExpenses.map { parseDate(it.date) }
        dates.minOrNull() to dates.maxOrNull()
    } else {
        LocalDate.now() to LocalDate.now()
    }
    val startDate = dateRange.first ?: LocalDate.now()
    val endDate = dateRange.second ?: LocalDate.now()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            MediumTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.inverseOnSurface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ), 
                title = {
                    Text(
                        "Expenses", 
                        maxLines = 1, 
                        overflow = TextOverflow.Ellipsis
                    )
                }, 
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                }, 
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        ShimmerProvider(isLoading = isLoading) {
            if (isLoading && expenses.isEmpty()) {
                ExpensesScreenSkeleton(paddingValues, scrollBehavior)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        BalanceCircles(
                            youOwe = balanceData.first,
                            youAreOwed = balanceData.second
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "History of expenses",
                            style = MaterialTheme.typography.headlineSmallEmphasized,
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .shimmerable()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    if (expensesByDate.isEmpty() && !isLoading) {
                        item {
                            EmptyExpensesState(
                                onAddExpenseClick = onAddExpenseClick
                            )
                        }
                    } else {
                        val dateRangeList = generateDateRange(startDate, endDate)
                        val sectionsWithExpenses = dateRangeList.filter { date ->
                            expensesByDate[date]?.isNotEmpty() == true
                        }

                        items(dateRangeList) { date ->
                            val isLastSectionWithExpenses =
                                sectionsWithExpenses.lastOrNull() == date
                            ExpensesDateSection(
                                date = date,
                                expenses = expensesByDate[date].orEmpty(),
                                isLastSection = isLastSectionWithExpenses,
                                onExpenseClick = { expenseId ->
                                    onExpenseClick()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BalanceCircles(
    youOwe: Double = 50.0, 
    youAreOwed: Double = 100.0
) {
    val totalAmount = youOwe + youAreOwed
    val (oweSize, owedSize) = if (totalAmount > 0) {
        calculateCircleSizes(youOwe, youAreOwed)
    } else {
        140.dp to 140.dp
    }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(oweSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiary)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "You owe",
                        style = MaterialTheme.typography.labelLargeEmphasized.copy(color = MaterialTheme.colorScheme.surface)
                    )
                    Text(
                        text = "$${String.format("%.2f", youOwe)}",
                        style = MaterialTheme.typography.titleLargeEmphasized.copy(color = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.shimmerable()
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .size(owedSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "You are owed",
                        style = MaterialTheme.typography.labelLargeEmphasized.copy(color = MaterialTheme.colorScheme.onTertiaryContainer)
                    )
                    Text(
                        text = "$${String.format("%.2f", youAreOwed)}",
                        style = MaterialTheme.typography.titleLargeEmphasized.copy(
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        ),
                        modifier = Modifier.shimmerable()
                    )
                }
            }
        }
    }
}

private fun calculateCircleSizes(youOwe: Double, youAreOwed: Double): Pair<Dp, Dp> {
    val minSize = 120.dp
    val maxSize = 180.dp
    val defaultSize = 140.dp
    
    if (youOwe < 0.01 && youAreOwed < 0.01) {
        return defaultSize to defaultSize
    }
    
    val totalAmount = youOwe + youAreOwed
    val difference = kotlin.math.abs(youOwe - youAreOwed)
    
    if (difference / totalAmount < 0.1) {
        return defaultSize to defaultSize
    }
    
    return when {
        youOwe > youAreOwed -> {
            val ratio = (youOwe / youAreOwed).coerceAtMost(1.5) 
            val oweSize = (defaultSize.value + (maxSize.value - defaultSize.value) * (ratio - 1) / 0.5).dp
            val owedSize = (defaultSize.value - (defaultSize.value - minSize.value) * (ratio - 1) / 0.5).dp
            oweSize to owedSize.coerceAtLeast(minSize)
        }
        youAreOwed > youOwe -> {
            val ratio = (youAreOwed / youOwe).coerceAtMost(1.5) 
            val owedSize = (defaultSize.value + (maxSize.value - defaultSize.value) * (ratio - 1) / 0.5).dp
            val oweSize = (defaultSize.value - (defaultSize.value - minSize.value) * (ratio - 1) / 0.5).dp
            oweSize.coerceAtLeast(minSize) to owedSize
        }
        else -> defaultSize to defaultSize
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpensesDateSection(
    date: LocalDate,
    expenses: List<ExpenseDisplayItem>,
    isLastSection: Boolean,
    onExpenseClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        DateRangeToolbar(date = date)

        Column(
            modifier = Modifier
                    .weight (1f)
                .padding(start = 8.dp)
        ) {
            if (expenses.isEmpty()) {
                // Show centered message when no expenses for this date
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "NO EXPENSE FOR THIS DATE",
                        style = MaterialTheme.typography.labelMediumEmphasized,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                expenses.forEach { item ->
                    ExpenseCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        expenseName = item.expenseName,
                        membersCount = item.membersCount,
                        amountOwed = item.amountOwed,
                        isCompleted = item.isCompleted,
                        isYours = item.isYours,
                        icon = item.icon
                    )
                }

                // Add horizontal divider only at the bottom of the last item for this day
                if (!isLastSection) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, top = 8.dp, end = 12.dp, bottom = 0.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpensesScreenSkeleton(
    paddingValues: PaddingValues,
    scrollBehavior: androidx.compose.material3.TopAppBarScrollBehavior
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            BalanceCircles(
                youOwe = 0.0,
                youAreOwed = 0.0
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "History of expenses",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .shimmerable()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(3) {
            Row {
                Column {
                    repeat(2) {
                        ExpenseCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .shimmerable(),
                            expenseName = "Loading expense...",
                            membersCount = 0,
                            amountOwed = 0.0,
                            isCompleted = false,
                            isYours = false,
                            icon = Icons.Filled.Money
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun EmptyExpensesState(onAddExpenseClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No expenses yet",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Start tracking your trip expenses",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onAddExpenseClick) {
            Text("Add Expense")
        }
    }
}

private fun getCurrentUserId(): Int {
    // TODO: Get current user ID from auth preferences or user session
    return 1 // Placeholder
}

private fun calculateBalanceData(expenses: List<UserExpenseSummary>): Pair<Double, Double> {
    val totalOwed = expenses.sumOf { it.userAmountOwed }
    val totalToReceive = expenses.sumOf { it.userAmountToReceive }
    return totalOwed to totalToReceive
}

private fun groupExpensesByDate(expenses: List<Expense>): Map<LocalDate, List<ExpenseDisplayItem>> {
    return expenses.groupBy { parseDate(it.date) }
        .mapValues { (_, expensesList) ->
            expensesList.map { expense ->
                ExpenseDisplayItem(
                    id = expense.id,
                    expenseDate = parseDate(expense.date),
                    expenseType = "", // Not available in current Expense model
                    expenseCategory = expense.category,
                    expenseName = expense.name,
                    membersCount = expense.debtors.size,
                    amountOwed = expense.amount,
                    isCompleted = expense.isCompleted,
                    isYours = getCurrentUserId() == expense.paidByUserId,
                    icon = Icons.Filled.Money // Default icon, could be mapped from category
                )
            }
        }
}

private fun parseDate(dateString: String): LocalDate {
    return try {
        LocalDate.parse(dateString)
    } catch (e: Exception) {
        LocalDate.now()
    }
}

@ThemePreviews
@Composable
private fun ExpensesScreenPreview() {
    val tripId = "mock-trip-id"
    val mockExpenses = listOf(
        UserExpenseSummary(
            totalTripExpenses = 150.0,
            userAmountOwed = 50.0,
            userAmountToReceive = 100.0,
            userBalance = 50.0,
            expenses = listOf(
                Expense(
                    id = 1,
                    tripId = 1,
                    name = "Dinner",
                    amount = 25.0,
                    date = LocalDate.now().toString(),
                    category = "Food",
                    paidByUserId = 1,
                    paymentMethod = "Cash",
                    splitType = "Equal",
                    notes = null,
                    isCompleted = false,
                    debtors = emptyList(),
                    paidBy = null
                ),
                Expense(
                    id = 2,
                    tripId = 1,
                    name = "Hotel",
                    amount = 100.0,
                    date = LocalDate.now().toString(),
                    category = "Accommodation",
                    paidByUserId = 2,
                    paymentMethod = "Credit Card",
                    splitType = "Equal",
                    notes = null,
                    isCompleted = false,
                    debtors = emptyList(),
                    paidBy = null
                ),
                Expense(
                    id = 3,
                    tripId = 1,
                    name = "Test",
                    amount = 100.0,
                    date = LocalDate.now().plusDays(1).toString(),
                    category = "Accommodation",
                    paidByUserId = 2,
                    paymentMethod = "Credit Card",
                    splitType = "Equal",
                    notes = null,
                    isCompleted = false,
                    debtors = emptyList(),
                    paidBy = null
                ),
            )
        )
    )

    PreviewWrapper {
        ExpensesScreen(
            navController = rememberNavController(),
            tripId = tripId,
            uiState = ExpensesUiState.Success(mockExpenses),
            expenses = mockExpenses,
            onRefresh = {},
            onSwiped = {},
            onExpenseClick = {},
            onAddExpenseClick = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@ThemePreviews
@Composable
private fun ExpensesScreenPreviewLoading() {
    val tripId = "mock-trip-id"

    PreviewWrapper {
        ExpensesScreen(
            navController = rememberNavController(),
            tripId = tripId,
            uiState = ExpensesUiState.Loading,
            expenses = emptyList(),
            onRefresh = {},
            onSwiped = {},
            onExpenseClick = {},
            onAddExpenseClick = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

data class ExpenseDisplayItem(
    val id: Int,
    val expenseDate: LocalDate,
    val expenseType: String,
    val expenseCategory: String,
    val expenseName: String,
    val membersCount: Int,
    val amountOwed: Double,
    val isCompleted: Boolean,
    val isYours: Boolean,
    val icon: ImageVector
)

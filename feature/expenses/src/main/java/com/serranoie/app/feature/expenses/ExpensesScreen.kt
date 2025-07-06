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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
                        items(generateDateRange(startDate, endDate)) { date ->
                            ExpensesDateSection(
                                date = date,
                                expenses = expensesByDate[date].orEmpty(),
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

// TODO: Replace with actual data
// TODO: Depending of total value of each data, change size depending of one or another.
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BalanceCircles(
    youOwe: Double = 50.0, youAreOwed: Double = 100.0
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            // Left Circle - You owe
            Box(
                modifier = Modifier
                    .size(140.dp)
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

            // Right Circle - You are owed
            Box(
                modifier = Modifier
                    .size(180.dp)
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

@Composable
fun ExpensesDateSection(
    date: LocalDate,
    expenses: List<ExpenseDisplayItem>,
    onExpenseClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        DateRangeToolbar(date = date)

        Column(
            modifier = Modifier.weight(1f)
        ) {
            expenses.forEach { item ->
                ExpenseCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, end = 16.dp),
                    expenseName = item.expenseName,
                    membersCount = item.membersCount,
                    amountOwed = item.amountOwed,
                    isCompleted = item.isCompleted,
                    isYours = item.isYours,
                    icon = item.icon
                )
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
                )
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

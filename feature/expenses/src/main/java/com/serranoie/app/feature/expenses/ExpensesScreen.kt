package com.serranoie.app.feature.expenses

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Money
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
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystemlib.ui.DevicePreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.theme.component.BalanceCircles
import com.serranoie.app.designsystemlib.ui.theme.component.DateRangeToolbar
import com.serranoie.app.designsystemlib.ui.theme.component.card.ExpenseCard
import com.serranoie.app.designsystemlib.ui.utils.Constants.basePadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.borderStrokeWidth
import com.serranoie.app.designsystemlib.ui.utils.Constants.extraSmallPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.largePadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.smallPadding
import com.serranoie.app.designsystemlib.ui.utils.ShimmerProvider
import com.serranoie.app.designsystemlib.ui.utils.shimmerable
import com.serranoie.app.designsystemlib.ui.utils.standardPadding
import com.serranoie.app.feature.expenses.domain.model.Expense
import com.serranoie.app.feature.expenses.domain.model.UserExpenseSummary
import com.serranoie.app.feature.expenses.util.ExpenseCategory
import com.serranoie.app.feature.expenses.util.ExpenseDisplayItem
import com.serranoie.app.feature.expenses.util.generateDateRange
import com.serranoie.app.feature.expenses.util.icon
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
    onExpenseClick: (String) -> Unit,
    onAddExpenseClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    currentUserId: Int
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val pullToRefreshState = rememberPullToRefreshState()

    val isLoading = uiState is ExpensesUiState.Loading
    val allExpenses = expenses.flatMap { it.expenses }
    val balanceData = calculateBalanceData(expenses)
    val expensesByDate = groupExpensesByDate(allExpenses, currentUserId)
    val dateRange = if (allExpenses.isNotEmpty()) {
        val dates = allExpenses.map { parseDate(it.date) }
        dates.minOrNull() to dates.maxOrNull()
    } else {
        LocalDate.now() to LocalDate.now()
    }
    val startDate = dateRange.first ?: LocalDate.now()
    val endDate = dateRange.second ?: LocalDate.now()

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, topBar = {
        MediumTopAppBar(
            title = {
                Text(
                    "Expenses", maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back"
                    )
                }
            }, scrollBehavior = scrollBehavior
        )
    }) { paddingValues ->
        AnimatedContent(
            targetState = uiState,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
            }
        ) { targetState ->
            if (targetState is ExpensesUiState.Loading && expenses.isEmpty()) {
                ShimmerProvider(isLoading = true) {
                    ExpensesScreenSkeleton(paddingValues, scrollBehavior)
                }
            } else {
                ExpensesContent(
                    expensesByDate = expensesByDate,
                    balanceData = balanceData,
                    startDate = startDate,
                    endDate = endDate,
                    isLoading = isLoading,
                    onRefresh = onRefresh,
                    onExpenseClick = onExpenseClick,
                    paddingValues = paddingValues,
                    scrollBehavior = scrollBehavior,
                    pullToRefreshState = pullToRefreshState
                )
            }
        }
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
            .padding(end = basePadding)
    ) {
        DateRangeToolbar(date = date)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = smallPadding)
        ) {
            if (expenses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "NO EXPENSE ON THIS DATE",
                        style = MaterialTheme.typography.labelSmallEmphasized,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.standardPadding()
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = smallPadding,
                            top = smallPadding,
                            end = smallPadding * 1.5f,
                            bottom = 0.dp
                        ),
                    thickness = borderStrokeWidth,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

            } else {
                expenses.forEach { item ->
                    ExpenseCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = extraSmallPadding),
                        expenseName = item.expenseName,
                        membersCount = item.membersCount,
                        amountOwed = item.amountOwed,
                        isCompleted = item.isCompleted,
                        isYours = item.isYours,
                        icon = item.icon
                    ) {
                        onExpenseClick(item.id.toString())
                    }
                }

                if (!isLastSection) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = smallPadding,
                                top = smallPadding,
                                end = smallPadding,
                                bottom = 0.dp
                            ),
                        thickness = borderStrokeWidth,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(smallPadding))
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpensesContent(
    expensesByDate: Map<LocalDate, List<ExpenseDisplayItem>>,
    balanceData: Pair<Double, Double>,
    startDate: LocalDate,
    endDate: LocalDate,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onExpenseClick: (String) -> Unit,
    paddingValues: PaddingValues,
    scrollBehavior: TopAppBarScrollBehavior,
    pullToRefreshState: PullToRefreshState
) {
    PullToRefreshBox(
        isRefreshing = isLoading, onRefresh = onRefresh, state = pullToRefreshState
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(bottom = basePadding)
        ) {
            if (expensesByDate.isEmpty() && !isLoading) {
                item {
                    EmptyExpensesState()
                }
            } else {
                item {
                    BalanceCircles(
                        youOwe = balanceData.first, youAreOwed = balanceData.second
                    )
                    Spacer(modifier = Modifier.height(smallPadding))
                    Text(
                        text = "History of expenses",
                        style = MaterialTheme.typography.headlineSmallEmphasized,
                        modifier = Modifier
                            .standardPadding(
                                horizontal = basePadding,
                                vertical = smallPadding
                            )
                    )
                    Spacer(modifier = Modifier.height(smallPadding))
                }

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
                            onExpenseClick(expenseId)
                        })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpensesScreenSkeleton(
    paddingValues: PaddingValues, scrollBehavior: TopAppBarScrollBehavior
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentPadding = PaddingValues(bottom = basePadding)
    ) {
        item {
            BalanceCircles(
                youOwe = 0.0, youAreOwed = 0.0,
                modifier = Modifier.shimmerable()
            )
            Spacer(modifier = Modifier.height(smallPadding))
            Text(
                text = "History of expenses",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .padding(horizontal = basePadding, vertical = smallPadding)
                    .shimmerable()
            )
            Spacer(modifier = Modifier.height(smallPadding))
        }

        items(3) {
            Row {
                Column {
                    repeat(2) {
                        ExpenseCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = basePadding,
                                    vertical = extraSmallPadding
                                )
                                .shimmerable(),
                            expenseName = "Loading expense...",
                            membersCount = 0,
                            amountOwed = 0.0,
                            isCompleted = false,
                            isYours = false,
                            icon = Icons.Filled.Money
                        ) {}

                        Spacer(modifier = Modifier.height(extraSmallPadding))
                    }
                }
            }
            Spacer(modifier = Modifier.height(smallPadding))
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun EmptyExpensesState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(largePadding - extraSmallPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35F),
            painter = painterResource(id = R.drawable.expenses_image),
            contentDescription = null,
            contentScale = ContentScale.Fit
        )

        Text(
            text = "No expenses yet",
            style = MaterialTheme.typography.headlineMediumEmphasized,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(smallPadding))
        Text(
            text = "Start tracking your group expenses by creating a new one.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun calculateBalanceData(expenses: List<UserExpenseSummary>): Pair<Double, Double> {
    val totalOwed = expenses.sumOf { it.userAmountOwed }
    val totalToReceive = expenses.sumOf { it.userAmountToReceive }
    return totalOwed to totalToReceive
}

private fun groupExpensesByDate(
    expenses: List<Expense>, currentUserId: Int
): Map<LocalDate, List<ExpenseDisplayItem>> {
    return expenses.groupBy { parseDate(it.date) }.mapValues { (_, expensesList) ->
            expensesList.map { expense ->
                val categoryEnum = ExpenseCategory.fromCategoryName(expense.category)
                ExpenseDisplayItem(
                    id = expense.id,
                    expenseDate = parseDate(expense.date),
                    expenseType = "",
                    expenseCategory = expense.category,
                    expenseName = expense.name,
                    membersCount = expense.debtors.size,
                    amountOwed = expense.amount,
                    isCompleted = expense.isCompleted,
                    isYours = currentUserId == expense.paidByUserId,
                    icon = categoryEnum.icon()
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

@DevicePreview
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
                    id = 4,
                    tripId = 1,
                    name = "Test",
                    amount = 100.0,
                    date = LocalDate.now().plusDays(2).toString(),
                    category = "Accommodation",
                    paidByUserId = 2,
                    paymentMethod = "Credit Card",
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
            onExpenseClick = { _ -> },
            onAddExpenseClick = {},
            snackbarHostState = remember { SnackbarHostState() },
            currentUserId = 1
        )
    }
}

@DevicePreview
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
            onExpenseClick = { _ -> },
            onAddExpenseClick = {},
            snackbarHostState = remember { SnackbarHostState() },
            currentUserId = 1
        )
    }
}

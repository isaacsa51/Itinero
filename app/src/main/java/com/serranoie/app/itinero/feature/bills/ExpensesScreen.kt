package com.serranoie.app.itinero.feature.bills

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
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ConfirmationNumber
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Shop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystem.ui.PreviewWrapper
import com.serranoie.app.designsystem.ui.ThemePreviews
import com.serranoie.app.designsystem.ui.theme.component.DateRangeToolbar
import com.serranoie.app.designsystem.ui.theme.component.ExpenseCard
import com.serranoie.app.itinero.navigation.bottombar.BottomBarNav
import com.serranoie.app.itinero.utils.generateDateRange
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(navController: NavController, expenses: Map<LocalDate, List<ExpenseItem>>) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val startDate = expenses.keys.minOrNull() ?: LocalDate.now()
    val endDate = expenses.keys.maxOrNull() ?: startDate

    Scaffold(topBar = {
        MediumTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.inverseOnSurface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
            ), title = {
                Text(
                    "Expenses", maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }, content = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back"
                    )
                })
            }, scrollBehavior = scrollBehavior
        )
    }, bottomBar = { BottomBarNav(navController = navController) }, floatingActionButton = {
        FloatingActionButton(
            onClick = { /* Handle FAB click */ },
            content = { Icon(Icons.Rounded.Add, contentDescription = "Add") },
        )
    }) { paddingValues ->
        val expenseState = remember { mutableStateOf(expenses) }
        val currentExpense by expenseState

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                BalanceCircles()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "History of expenses",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(generateDateRange(startDate, endDate)) { date ->
                ExpensesDateSection(
                    date = date,
                    expenses = currentExpense[date].orEmpty(),
                )
            }
        }
    }
}

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
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.surface)
                    )
                    Text(
                        text = "$${String.format("%.2f", youOwe)}",
                        style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.surface)
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
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onTertiaryContainer)
                    )
                    Text(
                        text = "$${String.format("%.2f", youAreOwed)}",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ExpensesDateSection(date: LocalDate, expenses: List<ExpenseItem>) {
    Row {
        DateRangeToolbar(date = date)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            if (expenses.isEmpty()) {
                Text(
                    text = "NO ITEMS ADDED AT THIS DATE",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                expenses.forEach { item ->
                    ExpenseCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        expenseName = item.expenseName,
                        membersCount = item.membersCount,
                        amountOwed = item.amountOwed,
                        isCompleted = item.isCompleted,
                        isYours = item.isYours,
                        icon = item.icon,
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp
            )
        }
    }
}

@ThemePreviews
@Composable
private fun ExpensesScreenPreview() {
    // Create a date range spanning a week
    val currentDate = LocalDate.now()
    val startDate = currentDate.minusDays(3)
    val endDate = currentDate.plusDays(3)
    
    // Create mock expenses across multiple days with various states
    val mockExpenses = mapOf(
        startDate to listOf(
            ExpenseItem(
                id = 1,
                expenseDate = startDate,
                expenseType = "Food",
                expenseCategory = "Groceries",
                expenseName = "Supermarket Shopping",
                membersCount = 4,
                amountOwed = 27.50,
                isCompleted = false,
                isYours = true,
                icon = Icons.Filled.Shop
            ),
            ExpenseItem(
                id = 2,
                expenseDate = startDate,
                expenseType = "Transportation",
                expenseCategory = "Taxi",
                expenseName = "Airport Transfer",
                membersCount = 3,
                amountOwed = 15.33,
                isCompleted = true,
                isYours = false,
                icon = Icons.Default.Money
            )
        ),
        startDate.plusDays(1) to listOf(
            ExpenseItem(
                id = 3,
                expenseDate = startDate.plusDays(1),
                expenseType = "Accommodation",
                expenseCategory = "Hotel",
                expenseName = "Beach Resort - Room #204",
                membersCount = 2,
                amountOwed = 120.00,
                isCompleted = true,
                isYours = false,
                icon = Icons.Default.Money
            )
        ),
        currentDate to listOf(
            ExpenseItem(
                id = 4,
                expenseDate = currentDate,
                expenseType = "Food",
                expenseCategory = "Restaurant",
                expenseName = "Dinner at La Taquería",
                membersCount = 4,
                amountOwed = 18.25,
                isCompleted = false,
                isYours = false,
                icon = Icons.Filled.Restaurant
            ),
            ExpenseItem(
                id = 5,
                expenseDate = currentDate,
                expenseType = "Entertainment",
                expenseCategory = "Movies",
                expenseName = "Cinema Tickets",
                membersCount = 3,
                amountOwed = 12.50,
                isCompleted = false,
                isYours = true,
                icon = Icons.Rounded.ConfirmationNumber
            ),
            ExpenseItem(
                id = 6,
                expenseDate = currentDate,
                expenseType = "Food",
                expenseCategory = "Snacks",
                expenseName = "Ice Cream Stop",
                membersCount = 5,
                amountOwed = 6.40,
                isCompleted = true,
                isYours = false,
                icon = Icons.Filled.Restaurant
            )
        ),
        endDate to emptyList() // Day with no expenses to show empty state
    )

    PreviewWrapper {
        ExpensesScreen(
            navController = rememberNavController(),
            expenses = mockExpenses
        )
    }
}

data class ExpenseItem(
    val id: Int,
    val expenseDate: LocalDate,
    val expenseType: String,
    val expenseCategory: String,
    val expenseName: String,
    val membersCount: Int,
    val amountOwed: Double,
    val isCompleted: Boolean = false,
    val isYours: Boolean = false,
    val icon: ImageVector = Icons.Default.Money,
)


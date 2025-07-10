package com.serranoie.app.feature.expenses

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.ThemePreviews
import com.serranoie.app.feature.expenses.util.ExpenseCategory

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailsScreen(
    navController: NavController,
    expenseState: ExpenseDetailsViewModel.ExpenseState,
    formUiState: ExpenseDetailsViewModel.UIState,
    splitType: SplitType,
    groupMembers: List<GroupMember>,
    persons: List<String>,
    paymentMethods: List<String>,
    onExpenseNameChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onShowDatePicker: (Boolean) -> Unit,
    onCategoryChange: (ExpenseCategory) -> Unit,
    onShowCategoryDropdownChange: (Boolean) -> Unit,
    onPaidByChange: (String?) -> Unit,
    onShowPersonsDropdownChange: (Boolean) -> Unit,
    onPaymentMethodChange: (String) -> Unit,
    onSplitTypeChange: (SplitType) -> Unit,
    onToggleMemberIncluded: (Int, Boolean) -> Unit,
    onUpdateMemberPercentage: (Int, Int) -> Unit,
    onUpdateMemberAmount: (Int, Double) -> Unit,
    onNotesChange: (String) -> Unit,
    onSaveExpense: () -> Unit,
    onClearErrorMessage: () -> Unit,
    onDateSelected: (String) -> Unit,
    isPercentageValid: Boolean,
    isManualAmountValid: Boolean
) {
    // For now, we'll use the existing static UI
    // In the future, this can be replaced with the actual expense details editing UI
    // that uses the ViewModel parameters
    ExpenseDetailsScreenStatic(navController)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ExpenseDetailsScreenStatic(
    navController: NavController,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                title = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Expense name",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
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
                scrollBehavior = scrollBehavior
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    // Top - Total Bill with primary background
                    TopBillSection()

                    // Paid By section
                    PaidBySection()

                    // Bottom - Owes section
                    OwesSection()
                }
            }
        }
    }
}

@Composable
fun TopBillSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            "Total Bill",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.surface
        )
        Text(
            "$250.00",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun PaidBySection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Paid By",
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(Modifier.height(8.dp))
            PaidByItem("Theresa Webb", "$150.00")
            PaidByItem("Marvin McKinney", "$100.00")

            Spacer(modifier = Modifier.height(16.dp))
        }

        CutoutTicketDivider()
    }
}

@Composable
fun PaidByItem(name: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        /*Image(
            painter = painterResource(id = avatar),
            contentDescription = null,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
        )*/
        Spacer(Modifier.width(8.dp))
        Text(name, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface)
        Text(amount, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun OwesSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OwesItem("Devon Lane", "Ralph Edwards", "$100.00" /*R.drawable.devon, R.drawable.ralph*/)
        OwesItem(
            "Bessie Cooper",
            "Esther Howard",
            "$100.00", /*R.drawable.bessie, R.drawable.esther*/
        )
        OwesItem(
            "Floyd Miles",
            "Kathryn Murphy",
            "$100.00", /*R.drawable.floyd, R.drawable.kathryn*/
        )
    }
}

@Composable
fun OwesItem(
    payer: String,
    owedTo: String,
    amount: String, /*@DrawableRes payerAvatar: Int, @DrawableRes receiverAvatar: Int*/
) {
    Column(Modifier.padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            /*Image(
                painter = painterResource(id = payerAvatar),
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
            )*/
            Spacer(Modifier.width(8.dp))
            Text(payer)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 48.dp)
        ) {
            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.width(8.dp))/*Image(
                painter = painterResource(id = receiverAvatar),
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
            )*/
            Spacer(Modifier.width(8.dp))
            Text(owedTo, modifier = Modifier.weight(1f))
            Text(amount, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun CutoutTicketDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    circleRadius: Dp = 14.dp,
    rectWidth: Dp = 10.dp,
    rectHeight: Dp = 6.dp,
    cornerRadius: Dp = 3.dp,
    spacingWidth: Dp = 16.dp
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

@ThemePreviews
@Composable
private fun ExpenseDetailsScreenPreview() {
    PreviewWrapper {
        ExpenseDetailsScreen(
            navController = rememberNavController(),
            expenseState = ExpenseDetailsViewModel.ExpenseState(),
            formUiState = ExpenseDetailsViewModel.UIState(),
            splitType = SplitType.EQUAL,
            groupMembers = listOf(),
            persons = listOf(),
            paymentMethods = listOf(),
            onExpenseNameChange = {},
            onAmountChange = {},
            onDateChange = {},
            onShowDatePicker = {},
            onCategoryChange = {},
            onShowCategoryDropdownChange = {},
            onPaidByChange = {},
            onShowPersonsDropdownChange = {},
            onPaymentMethodChange = {},
            onSplitTypeChange = {},
            onToggleMemberIncluded = { _, _ -> },
            onUpdateMemberPercentage = { _, _ -> },
            onUpdateMemberAmount = { _, _ -> },
            onNotesChange = {},
            onSaveExpense = {},
            onClearErrorMessage = {},
            onDateSelected = {},
            isPercentageValid = true,
            isManualAmountValid = true
        )
    }
}

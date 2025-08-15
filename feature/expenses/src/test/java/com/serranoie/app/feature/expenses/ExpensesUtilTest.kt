package com.serranoie.app.feature.expenses

import com.serranoie.app.feature.expenses.util.ExpenseCategory
import com.serranoie.app.feature.expenses.util.ExpenseSplitType
import com.serranoie.app.feature.expenses.util.generateDateRange
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class ExpensesUtilTest {

    @Test
    fun generateDateRange_inclusive() {
        val start = LocalDate.of(2025, 1, 1)
        val end = LocalDate.of(2025, 1, 3)
        val range = generateDateRange(start, end)
        assertEquals(listOf(start, start.plusDays(1), end), range)
    }

    @Test
    fun expenseCategory_fromName_isCaseInsensitive() {
        assertEquals(ExpenseCategory.FOOD, ExpenseCategory.fromCategoryName("food"))
        assertEquals(
            ExpenseCategory.ACCOMMODATION,
            ExpenseCategory.fromCategoryName("accommodation")
        )
        assertEquals(ExpenseCategory.MISC, ExpenseCategory.fromCategoryName("unknown"))
    }

    @Test
    fun expenseSplitType_fromName_supportsCustom() {
        assertEquals(ExpenseSplitType.MANUAL, ExpenseSplitType.fromSplitTypeName("custom"))
        assertEquals(ExpenseSplitType.MANUAL, ExpenseSplitType.fromSplitTypeName("manual"))
        assertEquals(ExpenseSplitType.EQUAL, ExpenseSplitType.fromSplitTypeName("equal"))
    }
}
package com.serranoie.app.feature.expenses

import androidx.annotation.VisibleForTesting
import com.serranoie.app.feature.expenses.domain.model.Expense
import com.serranoie.app.feature.expenses.domain.model.ExpenseDebtor
import com.serranoie.app.feature.expenses.domain.model.UserExpenseSummary
import com.serranoie.app.feature.expenses.domain.repository.ExpensesRepository
import com.serranoie.app.feature.expenses.domain.usecase.AddExpenseUseCase
import com.serranoie.app.feature.expenses.domain.usecase.DeleteExpenseUseCase
import com.serranoie.app.feature.expenses.domain.usecase.ExpensesUseCases
import com.serranoie.app.feature.expenses.domain.usecase.GetExpenseByIdUseCase
import com.serranoie.app.feature.expenses.domain.usecase.GetUserExpensesUseCase
import com.serranoie.app.feature.expenses.domain.usecase.MarkDebtorAsPaidUseCase
import com.serranoie.app.feature.expenses.domain.usecase.MarkDebtorAsUnpaidUseCase
import com.serranoie.app.feature.expenses.domain.usecase.UpdateExpenseUseCase
import com.serranoie.itinero.core.domain.repository.AuthPreferencesRepository
import com.serranoie.itinero.core.domain.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExpensesViewModelTest {

    private lateinit var fakeRepo: FakeExpensesRepository
    private lateinit var useCases: ExpensesUseCases
    private lateinit var authPrefs: FakeAuthPrefs

    @Before
    fun setup() {
        fakeRepo = FakeExpensesRepository()
        useCases = ExpensesUseCases(
            getUserExpensesUseCase = GetUserExpensesUseCase(fakeRepo),
            getExpenseByIdUseCase = GetExpenseByIdUseCase(fakeRepo),
            updateExpenseUseCase = UpdateExpenseUseCase(fakeRepo),
            deleteExpenseUseCase = DeleteExpenseUseCase(fakeRepo),
            addExpenseUseCase = AddExpenseUseCase(fakeRepo),
            markDebtorAsPaidUseCase = MarkDebtorAsPaidUseCase(fakeRepo),
            markDebtorAsUnpaidUseCase = MarkDebtorAsUnpaidUseCase(fakeRepo)
        )
        authPrefs = FakeAuthPrefs(userId = 1)
    }

    @Test
    fun fetchUserExpenseSummaries_emitsProcessedData() = runBlocking {
        val vm = ExpensesViewModel(useCases, authPrefs)

        val debtorPaid =
            ExpenseDebtor(id = 1, userId = 1, amount = 10.0, splitValue = 50.0, hasPaid = true)
        val debtorUnpaid =
            ExpenseDebtor(id = 2, userId = 2, amount = 10.0, splitValue = 50.0, hasPaid = false)
        val expenses = listOf(
            Expense(
                id = 10,
                tripId = 1,
                name = "Dinner",
                amount = 20.0,
                date = "2025-01-01",
                category = "Food",
                paidByUserId = 1,
                paymentMethod = "Cash",
                splitType = "EQUAL",
                debtors = listOf(debtorPaid, debtorUnpaid)
            ),
            Expense(
                id = 11,
                tripId = 1,
                name = "Taxi",
                amount = 20.0,
                date = "2025-01-01",
                category = "Transport",
                paidByUserId = 1,
                paymentMethod = "Cash",
                splitType = "EQUAL",
                debtors = listOf(debtorPaid.copy(hasPaid = true), debtorPaid.copy(userId = 2))
            )
        )
        fakeRepo.userSummaryFlow.emit(
            listOf(
                UserExpenseSummary(
                    totalTripExpenses = 40.0,
                    userAmountOwed = 10.0,
                    userAmountToReceive = 0.0,
                    userBalance = -10.0,
                    expenses = expenses
                )
            )
        )

        vm.fetchUserExpenseSummaries(groupCode = "1")

        val summaries = TestUtils.waitForCondition(vm.userExpenseSummaries, { it.isNotEmpty() })

        assertEquals(2, summaries.first().expenses.size)
        val processed = summaries.first().expenses
        assertTrue(!processed[0].isCompleted)
        assertTrue(processed[1].isCompleted)
    }

    @Test
    fun getExpenseById_success_setsSelectedExpense() = runBlocking {
        val vm = ExpensesViewModel(useCases, authPrefs)
        val expense = Expense(
            id = 5,
            tripId = 1,
            name = "Coffee",
            amount = 3.5,
            date = "2025-01-02",
            category = "Food",
            paidByUserId = 1,
            paymentMethod = "Cash",
            splitType = "EQUAL",
            debtors = emptyList()
        )
        fakeRepo.expenseByIdResult = Result.Success(expense)

        vm.getExpenseById("1", "5")

        val selected = TestUtils.waitForCondition(vm.selectedExpense, { it != null })
        assertEquals(5, selected?.id)
        assertTrue(vm.uiState.value is ExpensesUiState.Success<*>)
    }
}

@VisibleForTesting
object TestUtils {
    suspend fun <T> waitForCondition(
        stateFlow: kotlinx.coroutines.flow.StateFlow<T>,
        predicate: (T) -> Boolean,
        attempts: Int = 50,
        delayMs: Long = 20
    ): T {
        var last: T = stateFlow.value
        repeat(attempts) {
            last = stateFlow.value
            if (predicate(last)) return last
            kotlinx.coroutines.delay(delayMs)
        }
        return last
    }
}

class FakeAuthPrefs(private val userId: Int?) : AuthPreferencesRepository {
    override fun saveToken(token: String) {}
    override fun getToken(): String? = null
    override fun saveUserId(userId: Int) {}
    override fun getUserId(): Int? = userId
    override fun setOnboardingCompleted() {}
    override fun isOnboardingCompleted(): Boolean = true
    override fun saveLoginStatus(isLoggedIn: Boolean, expirationTimeMillis: Long?) {}
    override fun isUserLoggedIn(): Boolean = true
    override fun clearLoginStatus() {}
    override fun clearToken() {}
    override fun saveUserName(name: String) {}
    override fun getUserName(): String? = null
    override fun saveUserLastName(lastName: String) {}
    override fun getUserLastName(): String? = null
    override fun saveUserEmail(email: String) {}
    override fun getUserEmail(): String? = null
    override fun saveUserPhone(phone: String) {}
    override fun getUserPhone(): String? = null
    override fun clearUserInfo() {}
    override fun getUserProfile(): com.serranoie.itinero.core.domain.model.UserProfile? = null
    override fun setUserProfile(profile: com.serranoie.itinero.core.domain.model.UserProfile) {}
}

class FakeExpensesRepository : ExpensesRepository {
    val userSummaryFlow = MutableSharedFlow<List<UserExpenseSummary>>(replay = 1)
    var expenseByIdResult: Result<Expense> = Result.Error(Exception("Not set"))

    override suspend fun createExpense(
        groupCode: String,
        expense: com.serranoie.app.feature.expenses.domain.model.CreateExpense
    ): Result<Expense> {
        return Result.Success(
            Expense(
                id = 99,
                tripId = expense.tripId,
                name = expense.name,
                amount = expense.amount,
                date = expense.date,
                category = expense.category,
                paidByUserId = expense.paidByUserId,
                paymentMethod = expense.paymentMethod,
                splitType = expense.splitType
            )
        )
    }

    override suspend fun getAllTripExpenses(groupCode: String): Result<List<Expense>> =
        Result.Success(emptyList())

    override suspend fun getUserExpenseSummary(groupCode: String): Flow<List<UserExpenseSummary>> =
        userSummaryFlow.asSharedFlow()

    override suspend fun getExpenseById(groupCode: String, expenseId: String): Result<Expense> =
        expenseByIdResult
    override suspend fun updateExpense(
        groupCode: String,
        expenseId: String,
        expense: com.serranoie.app.feature.expenses.domain.model.CreateExpense
    ): Result<Unit> = Result.Success(Unit)
    override suspend fun deleteExpense(groupCode: String, expenseId: String): Result<Unit> =
        Result.Success(Unit)

    override suspend fun markExpenseCompleted(groupCode: String, expenseId: String): Result<Unit> =
        Result.Success(Unit)

    override suspend fun markDebtorAsPaid(groupCode: String, expenseId: String): Result<Unit> =
        Result.Success(Unit)

    override suspend fun markDebtorAsUnpaid(groupCode: String, expenseId: String): Result<Unit> =
        Result.Success(Unit)

    override fun getAllTripExpensesFlow(groupCode: String): Flow<List<Expense>> =
        flowOf(emptyList())
    override fun getUserExpenseSummaryFlow(groupCode: String): Flow<com.serranoie.app.feature.expenses.domain.model.UserExpenseSummary> =
        flowOf(
            com.serranoie.app.feature.expenses.domain.model.UserExpenseSummary(
                0.0,
                0.0,
                0.0,
                0.0,
                emptyList()
            )
        )

    override fun getExpenseByIdFlow(groupCode: String, expenseId: String): Flow<Expense> = flowOf()
    override suspend fun clearCache(): Result<Unit> = Result.Success(Unit)
    override suspend fun refreshExpenses(groupCode: String): Result<Unit> = Result.Success(Unit)
}
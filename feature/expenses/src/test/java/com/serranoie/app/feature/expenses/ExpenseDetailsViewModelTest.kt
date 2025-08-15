package com.serranoie.app.feature.expenses

import com.serranoie.app.feature.expenses.domain.model.Expense
import com.serranoie.app.feature.expenses.domain.model.ExpenseDebtor
import com.serranoie.app.feature.expenses.domain.repository.ExpensesRepository
import com.serranoie.app.feature.expenses.domain.usecase.AddExpenseUseCase
import com.serranoie.app.feature.expenses.domain.usecase.DeleteExpenseUseCase
import com.serranoie.app.feature.expenses.domain.usecase.ExpensesUseCases
import com.serranoie.app.feature.expenses.domain.usecase.GetExpenseByIdUseCase
import com.serranoie.app.feature.expenses.domain.usecase.GetUserExpensesUseCase
import com.serranoie.app.feature.expenses.domain.usecase.MarkDebtorAsPaidUseCase
import com.serranoie.app.feature.expenses.domain.usecase.MarkDebtorAsUnpaidUseCase
import com.serranoie.app.feature.expenses.domain.usecase.UpdateExpenseUseCase
import com.serranoie.itinero.core.domain.model.MemberStatus
import com.serranoie.itinero.core.domain.model.TripMember
import com.serranoie.itinero.core.domain.repository.AuthPreferencesRepository
import com.serranoie.itinero.core.domain.repository.TravelRepository
import com.serranoie.itinero.core.domain.result.Result
import com.serranoie.itinero.core.domain.usecase.AcceptMemberToTripUseCase
import com.serranoie.itinero.core.domain.usecase.CreateTravelUseCase
import com.serranoie.itinero.core.domain.usecase.DeleteTripUseCase
import com.serranoie.itinero.core.domain.usecase.GetAllMembersUseCase
import com.serranoie.itinero.core.domain.usecase.GetAllTravelsUseCase
import com.serranoie.itinero.core.domain.usecase.GetCurrentUserMembershipStatusUseCase
import com.serranoie.itinero.core.domain.usecase.GetTravelByIdUseCase
import com.serranoie.itinero.core.domain.usecase.GetTripOverviewUseCase
import com.serranoie.itinero.core.domain.usecase.JoinTravelUseCase
import com.serranoie.itinero.core.domain.usecase.LeaveTravelUseCase
import com.serranoie.itinero.core.domain.usecase.LeaveTripUseCase
import com.serranoie.itinero.core.domain.usecase.MakeOwnerUseCase
import com.serranoie.itinero.core.domain.usecase.RejectMemberUseCase
import com.serranoie.itinero.core.domain.usecase.RemoveMemberUseCase
import com.serranoie.itinero.core.domain.usecase.TravelUseCase
import com.serranoie.itinero.core.domain.usecase.UpdateTripInfoUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExpenseDetailsViewModelTest {

    private lateinit var expensesRepo: ExpenseDetailsFakeExpensesRepository
    private lateinit var expensesUseCases: ExpensesUseCases
    private lateinit var authPrefs: ExpenseDetailsFakeAuthPrefs
    private lateinit var travelUseCase: TravelUseCase

    @Before
    fun setup() {
        expensesRepo = ExpenseDetailsFakeExpensesRepository()
        expensesUseCases = ExpensesUseCases(
            getUserExpensesUseCase = GetUserExpensesUseCase(expensesRepo),
            getExpenseByIdUseCase = GetExpenseByIdUseCase(expensesRepo),
            updateExpenseUseCase = UpdateExpenseUseCase(expensesRepo),
            deleteExpenseUseCase = DeleteExpenseUseCase(expensesRepo),
            addExpenseUseCase = AddExpenseUseCase(expensesRepo),
            markDebtorAsPaidUseCase = MarkDebtorAsPaidUseCase(expensesRepo),
            markDebtorAsUnpaidUseCase = MarkDebtorAsUnpaidUseCase(expensesRepo)
        )
        authPrefs = ExpenseDetailsFakeAuthPrefs(userId = 1)

        val travelRepo = FakeTravelRepository(
            members = listOf(
                TripMember(
                    id = 1,
                    name = "Alice",
                    surname = "Smith",
                    email = "a@a.com",
                    status = MemberStatus.OWNER
                ),
                TripMember(
                    id = 2,
                    name = "Bob",
                    surname = "Lee",
                    email = "b@b.com",
                    status = MemberStatus.ACCEPTED
                )
            )
        )

        travelUseCase = TravelUseCase(
            getAllTravels = GetAllTravelsUseCase(travelRepo),
            getTravelById = GetTravelByIdUseCase(travelRepo),
            joinTravel = JoinTravelUseCase(travelRepo),
            leaveTravel = LeaveTravelUseCase(travelRepo),
            createTravel = CreateTravelUseCase(travelRepo),
            updateTripInfo = UpdateTripInfoUseCase(travelRepo),
            acceptMemberToTrip = AcceptMemberToTripUseCase(travelRepo),
            getAllMembers = GetAllMembersUseCase(travelRepo),
            rejectMember = RejectMemberUseCase(travelRepo),
            removeMember = RemoveMemberUseCase(travelRepo),
            makeOwner = MakeOwnerUseCase(travelRepo),
            getCurrentUserMembershipStatus = GetCurrentUserMembershipStatusUseCase(travelRepo),
            leaveTrip = LeaveTripUseCase(travelRepo),
            getTripOverview = GetTripOverviewUseCase(travelRepo),
            deleteTrip = DeleteTripUseCase(travelRepo)
        )
    }

    @Test
    fun equalSplit_recalculateAmounts_isEven() = runBlocking {
        val vm = ExpenseDetailsViewModel(
            expensesUseCase = expensesUseCases,
            travelUseCase = travelUseCase,
            authPreferencesRepository = authPrefs,
            groupCode = "1"
        )

        kotlinx.coroutines.delay(50)

        vm.updateSplitType(SplitType.EQUAL)
        vm.updateAmount("100")

        val members = awaitState(vm.groupMembers, predicate = { it.isNotEmpty() })
        val included = members.filter { it.included }
        assertEquals(2, included.size)
        assertTrue(included.all { kotlin.math.abs(it.amount - 50.0) < 0.001 })
    }

    @Test
    fun percentageSplit_validation() = runBlocking {
        val vm = ExpenseDetailsViewModel(
            expensesUseCase = expensesUseCases,
            travelUseCase = travelUseCase,
            authPreferencesRepository = authPrefs,
            groupCode = "1"
        )
        kotlinx.coroutines.delay(50)

        vm.updateSplitType(SplitType.PERCENTAGE)
        // Set 60/50 = invalid
        vm.updateMemberPercentage(0, 60)
        vm.updateMemberPercentage(1, 50)
        assertFalse(vm.isPercentageValid())
        // Fix to 50/50
        vm.updateMemberPercentage(0, 50)
        vm.updateMemberPercentage(1, 50)
        assertTrue(vm.isPercentageValid())
    }

    @Test
    fun manualSplit_validation() = runBlocking {
        val vm = ExpenseDetailsViewModel(
            expensesUseCase = expensesUseCases,
            travelUseCase = travelUseCase,
            authPreferencesRepository = authPrefs,
            groupCode = "1"
        )
        kotlinx.coroutines.delay(50)

        vm.updateSplitType(SplitType.CUSTOM)
        vm.updateAmount("100")
        vm.updateMemberAmount(0, 40.0)
        vm.updateMemberAmount(1, 40.0)
        assertFalse(vm.isManualAmountValid())

        vm.updateMemberAmount(0, 50.0)
        vm.updateMemberAmount(1, 50.0)
        assertTrue(vm.isManualAmountValid())
    }
}

private class ExpenseDetailsFakeAuthPrefs(private val userId: Int?) : AuthPreferencesRepository {
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

private class ExpenseDetailsFakeExpensesRepository : ExpensesRepository {
    override suspend fun createExpense(
        groupCode: String,
        expense: com.serranoie.app.feature.expenses.domain.model.CreateExpense
    ): Result<Expense> =
        Result.Success(
            Expense(
                id = 1,
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

    override suspend fun getAllTripExpenses(groupCode: String): Result<List<Expense>> =
        Result.Success(emptyList())

    override suspend fun getUserExpenseSummary(groupCode: String): Flow<List<com.serranoie.app.feature.expenses.domain.model.UserExpenseSummary>> =
        flowOf(emptyList())

    override suspend fun getExpenseById(groupCode: String, expenseId: String): Result<Expense> =
        Result.Success(
            Expense(
                id = expenseId.toIntOrNull() ?: 1,
                tripId = 1,
                name = "Mock",
                amount = 10.0,
                date = "2025-01-01",
                category = "Food",
                paidByUserId = 1,
                paymentMethod = "Cash",
                splitType = "EQUAL",
                debtors = listOf(
                    ExpenseDebtor(
                        id = 1,
                        userId = 1,
                        amount = 10.0,
                        splitValue = 100.0,
                        hasPaid = false
                    )
                )
        )
    )

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

private class FakeTravelRepository(private val members: List<TripMember>) : TravelRepository {
    override suspend fun getAllTravels(): Result<List<com.serranoie.itinero.core.domain.model.Trip>> =
        Result.Success(emptyList())

    override suspend fun getTravelById(
        groupCode: String,
        forceRefresh: Boolean
    ): Result<com.serranoie.itinero.core.domain.model.Trip> = Result.Error(Exception("Not used"))
    override suspend fun joinTravel(groupCode: String): Result<Unit> = Result.Success(Unit)
    override suspend fun createTravel(request: com.serranoie.itinero.core.domain.model.CreateTrip): Result<com.serranoie.itinero.core.domain.model.CreateTrip> =
        Result.Success(request)

    override suspend fun updateTripInfo(
        groupCode: String,
        request: com.serranoie.itinero.core.domain.model.UpdateTrip
    ): Result<com.serranoie.itinero.core.domain.model.Trip> = Result.Error(Exception("Not used"))

    override suspend fun getAllMembers(groupCode: String): Result<List<TripMember>> =
        Result.Success(members)

    override suspend fun acceptMember(groupCode: String, idMember: Int): Result<Unit> =
        Result.Success(Unit)

    override suspend fun rejectMember(groupCode: String, idMember: Int): Result<Unit> =
        Result.Success(Unit)

    override suspend fun removeMember(groupCode: String, idMember: Int): Result<Unit> =
        Result.Success(Unit)

    override suspend fun makeOwner(groupCode: String, idMember: Int): Result<Unit> =
        Result.Success(Unit)

    override suspend fun getCurrentUserMembershipStatus(groupCode: String): Result<com.serranoie.itinero.core.domain.model.MembershipStatus> =
        Result.Error(Exception("Not used"))
    override suspend fun leaveTrip(groupCode: String): Result<Unit> = Result.Success(Unit)
    override suspend fun getTripOverview(groupCode: String): Result<com.serranoie.itinero.core.domain.model.TripOverview> =
        Result.Error(Exception("Not used"))
    override suspend fun deleteTrip(groupCode: String): Result<Unit> = Result.Success(Unit)
}

private suspend fun <T> awaitState(
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
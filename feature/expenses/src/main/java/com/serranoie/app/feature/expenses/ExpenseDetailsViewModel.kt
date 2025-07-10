package com.serranoie.app.feature.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serranoie.app.feature.expenses.domain.model.CreateDebtor
import com.serranoie.app.feature.expenses.domain.model.CreateExpense
import com.serranoie.app.feature.expenses.domain.model.Expense
import com.serranoie.app.feature.expenses.domain.usecase.ExpensesUseCases
import com.serranoie.app.feature.expenses.util.ExpenseCategory
import com.serranoie.itinero.core.domain.model.MemberStatus
import com.serranoie.itinero.core.domain.model.TripMember
import com.serranoie.itinero.core.domain.result.Result
import com.serranoie.itinero.core.domain.usecase.TravelUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.abs


class ExpenseDetailsViewModel(
    private val expensesUseCase: ExpensesUseCases,
    private val travelUseCase: TravelUseCase,
    private val groupCode: String
) : ViewModel() {

    data class ExpenseState(
        val name: String = "",
        val amount: String = "",
        val date: String = LocalDate.now().format(DateTimeFormatter.ISO_DATE),
        val category: ExpenseCategory = ExpenseCategory.FOOD,
        val currency: String = "USD",
        val paidBy: String? = null,
        val paymentMethod: String = "Cash",
        val notes: String = "",
        val nameError: String? = null,
        val amountError: String? = null,
        val isSaving: Boolean = false
    )

    data class UIState(
        val showCategoryDropdown: Boolean = false,
        val showCurrencyDropdown: Boolean = false,
        val showPersonsDropdown: Boolean = false,
        val showPaymentMethodDropdown: Boolean = false,
        val showDatePicker: Boolean = false,
        val showSuccessMessage: Boolean = false,
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow<ExpensesUiState>(ExpensesUiState.Idle)
    val uiState: StateFlow<ExpensesUiState> = _uiState.asStateFlow()

    private val _expenseState = MutableStateFlow(ExpenseState())
    val expenseState = _expenseState.asStateFlow()

    private val _formUiState = MutableStateFlow(UIState())
    val formUiState = _formUiState.asStateFlow()

    private val _splitType = MutableStateFlow(SplitType.EQUAL)
    val splitType = _splitType.asStateFlow()

    private val _groupMembers = MutableStateFlow<List<GroupMember>>(emptyList())
    val groupMembers = _groupMembers.asStateFlow()

    private val _selectedExpense = MutableStateFlow<Expense?>(null)
    val selectedExpense: StateFlow<Expense?> = _selectedExpense.asStateFlow()

    private val _tripMembers = MutableStateFlow<List<TripMember>>(emptyList())
    val tripMembers = _tripMembers.asStateFlow()

    private val _currentUserId = MutableStateFlow<Int?>(null)
    val currentUserId = _currentUserId.asStateFlow()

    private var currentGroupCode: String = groupCode

    // Available options - now dynamic based on trip members
    val persons: List<String>
        get() = _tripMembers.value.filter { it.status == MemberStatus.ACCEPTED || it.status == MemberStatus.OWNER }
            .map { it.name }

    val paymentMethods = listOf("Cash", "Credit Card", "Debit Card", "Bank Transfer", "Other")

    init {
        fetchTripMembers()
    }

    private fun fetchTripMembers() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = travelUseCase.getAllMembers(currentGroupCode)) {
                is Result.Success -> {
                    _tripMembers.value = result.data
                    initializeGroupMembers(result.data)
                    // Set default paidBy to current user if available
                    val currentUser = result.data.find { it.status == MemberStatus.OWNER }
                        ?: result.data.firstOrNull { it.status == MemberStatus.ACCEPTED }
                    currentUser?.let { user ->
                        _currentUserId.value = user.id
                        _expenseState.update { it.copy(paidBy = user.name) }
                    }
                }

                is Result.Error -> {
                    // Fallback to default members if fetch fails
                    initializeDefaultMembers()
                }
            }
        }
    }

    private fun initializeGroupMembers(tripMembers: List<TripMember>) {
        val acceptedMembers = tripMembers.filter {
            it.status == MemberStatus.ACCEPTED || it.status == MemberStatus.OWNER
        }

        if (acceptedMembers.isNotEmpty()) {
            val equalPercentage = 100 / acceptedMembers.size
            val remainder = 100 % acceptedMembers.size

            val members = acceptedMembers.mapIndexed { index, member ->
                GroupMember(
                    name = member.name,
                    included = true,
                    percentage = equalPercentage + if (index < remainder) 1 else 0,
                    amount = 0.0,
                    userId = member.id
                )
            }
            _groupMembers.value = members
        } else {
            initializeDefaultMembers()
        }
    }

    private fun initializeDefaultMembers() {
        _groupMembers.value = listOf(
            GroupMember("Me", true, percentage = 100, amount = 0.0, userId = null)
        )
    }

    // CRUD Operations
    fun createExpense() {
        if (!validateExpense()) {
            _formUiState.update { it.copy(errorMessage = "Please correct the errors before saving") }
            return
        }

        _expenseState.update { it.copy(isSaving = true) }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ExpensesUiState.Loading
            try {
                // Find the payer's user ID
                val paidByMember = _tripMembers.value.find { it.name == _expenseState.value.paidBy }
                val paidByUserId = paidByMember?.id ?: _currentUserId.value ?: 1

                val request = CreateExpense(
                    tripId = currentGroupCode.toIntOrNull() ?: 1, // Convert groupCode to tripId
                    name = _expenseState.value.name,
                    amount = _expenseState.value.amount.toDouble(),
                    date = _expenseState.value.date,
                    category = _expenseState.value.category.name,
                    paidByUserId = paidByUserId,
                    paymentMethod = _expenseState.value.paymentMethod,
                    splitType = _splitType.value.name,
                    notes = _expenseState.value.notes.ifEmpty { null },
                    debtors = _groupMembers.value.filter { it.included }.map { member ->
                        CreateDebtor(
                            userId = member.userId ?: _currentUserId.value ?: 1,
                            splitValue = member.amount
                        )
                    }
                )

                when (val result =
                    expensesUseCase.addExpenseUseCase(currentGroupCode, request)) {
                    is Result.Success -> {
                        _uiState.value = ExpensesUiState.Success(result.data)
                        _formUiState.update {
                            it.copy(
                                showSuccessMessage = true,
                                errorMessage = null
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.value = ExpensesUiState.Error(
                            result.exception.message ?: "Failed to create expense"
                        )
                        _formUiState.update {
                            it.copy(
                                errorMessage = result.exception.message
                                    ?: "Failed to create expense"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = ExpensesUiState.Error("Failed to save: ${e.message}")
                _formUiState.update { it.copy(errorMessage = "Failed to save: ${e.message}") }
            } finally {
                _expenseState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun updateExpense(expenseId: String) {
        if (!validateExpense()) {
            _formUiState.update { it.copy(errorMessage = "Please correct the errors before updating") }
            return
        }

        _expenseState.update { it.copy(isSaving = true) }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ExpensesUiState.Loading
            try {
                val paidByMember = _tripMembers.value.find { it.name == _expenseState.value.paidBy }
                val paidByUserId = paidByMember?.id ?: _currentUserId.value ?: 1

                val request = CreateExpense(
                    tripId = currentGroupCode.toIntOrNull() ?: 1,
                    name = _expenseState.value.name,
                    amount = _expenseState.value.amount.toDouble(),
                    date = _expenseState.value.date,
                    category = _expenseState.value.category.name,
                    paidByUserId = paidByUserId,
                    paymentMethod = _expenseState.value.paymentMethod,
                    splitType = _splitType.value.name,
                    notes = _expenseState.value.notes.ifEmpty { null },
                    debtors = _groupMembers.value.filter { it.included }.map { member ->
                        CreateDebtor(
                            userId = member.userId ?: _currentUserId.value ?: 1,
                            splitValue = member.amount
                        )
                    }
                )

                when (val result =
                    expensesUseCase.updateExpenseUseCase(currentGroupCode, expenseId, request)) {
                    is Result.Success -> {
                        val updatedExpense =
                            expensesUseCase.getExpenseByIdUseCase(currentGroupCode, expenseId)
                        if (updatedExpense is Result.Success) {
                            _selectedExpense.value = updatedExpense.data
                            _uiState.value = ExpensesUiState.Success(updatedExpense.data)
                        } else {
                            _uiState.value = ExpensesUiState.Success(Unit)
                        }
                        _formUiState.update {
                            it.copy(
                                showSuccessMessage = true,
                                errorMessage = null
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.value = ExpensesUiState.Error(
                            result.exception.message ?: "Failed to update expense"
                        )
                        _formUiState.update {
                            it.copy(
                                errorMessage = result.exception.message
                                    ?: "Failed to update expense"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = ExpensesUiState.Error("Failed to update: ${e.message}")
                _formUiState.update { it.copy(errorMessage = "Failed to update: ${e.message}") }
            } finally {
                _expenseState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun getExpenseById(expenseId: String, forceRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ExpensesUiState.Loading
            when (val result =
                expensesUseCase.getExpenseByIdUseCase(currentGroupCode, expenseId)) {
                is Result.Success -> {
                    _selectedExpense.value = result.data
                    _uiState.value = ExpensesUiState.Success(result.data)
                    loadExpenseIntoForm(result.data)
                }
                is Result.Error -> {
                    _uiState.value = ExpensesUiState.Error(
                        result.exception.message ?: "Failed to load expense"
                    )
                }
            }
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ExpensesUiState.Loading
            when (val result = expensesUseCase.deleteExpenseUseCase(currentGroupCode, expenseId)) {
                is Result.Success -> {
                    _uiState.value = ExpensesUiState.Success(Unit)
                    _selectedExpense.value = null
                }
                is Result.Error -> {
                    _uiState.value = ExpensesUiState.Error(
                        result.exception.message ?: "Failed to delete expense"
                    )
                }
            }
        }
    }

    fun toggleExpenseCompletion(expenseId: String) {
        // This method doesn't exist in the current ExpensesUseCases
        // For now, we'll just show a placeholder implementation
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ExpensesUiState.Error("Toggle completion not implemented yet")
        }
    }

    private fun loadExpenseIntoForm(expense: Expense) {
        _expenseState.update {
            it.copy(
                name = expense.name,
                amount = expense.amount.toString(),
                date = expense.date,
                category = ExpenseCategory.entries.find { cat -> cat.name == expense.category }
                    ?: ExpenseCategory.FOOD,
                paidBy = expense.paidBy?.name,
                paymentMethod = expense.paymentMethod,
                notes = expense.notes ?: ""
            )
        }

        // Load split type from expense
        _splitType.value =
            SplitType.entries.find { it.name == expense.splitType } ?: SplitType.EQUAL

        // Load group members from expense debtors
        if (expense.debtors.isNotEmpty()) {
            val members = expense.debtors.map { debtor ->
                GroupMember(
                    name = debtor.user?.name ?: "Unknown",
                    included = debtor.amount > 0,
                    percentage = 0, // Would need to calculate from amount
                    amount = debtor.amount,
                    userId = debtor.userId
                )
            }
            _groupMembers.value = members
        }
    }

    // Input handlers
    fun updateExpenseName(name: String) {
        _expenseState.update {
            it.copy(
                name = name,
                nameError = if (name.isBlank()) "Name is required" else null
            )
        }
    }

    fun updateAmount(amount: String) {
        val amountError = when {
            amount.isBlank() -> "Amount is required"
            amount.toDoubleOrNull() == null -> "Invalid amount"
            amount.toDoubleOrNull()!! <= 0 -> "Amount must be positive"
            else -> null
        }

        _expenseState.update { it.copy(amount = amount, amountError = amountError) }

        if (amountError == null) {
            recalculateAmounts()
        }
    }

    fun updateDate(date: String) {
        _expenseState.update { it.copy(date = date) }
    }

    fun updateCategory(category: ExpenseCategory) {
        _expenseState.update { it.copy(category = category) }
        _formUiState.update { it.copy(showCategoryDropdown = false) }
    }

    fun updatePaidBy(paidBy: String?) {
        _expenseState.update { it.copy(paidBy = paidBy) }
        _formUiState.update { it.copy(showPersonsDropdown = false) }
    }

    fun updatePaymentMethod(method: String) {
        _expenseState.update { it.copy(paymentMethod = method) }
        _formUiState.update { it.copy(showPaymentMethodDropdown = false) }
    }

    fun updateNotes(notes: String) {
        _expenseState.update { it.copy(notes = notes) }
    }

    // UI state handlers
    fun toggleCategoryDropdown(show: Boolean) {
        _formUiState.update { it.copy(showCategoryDropdown = show) }
    }

    fun toggleCurrencyDropdown(show: Boolean) {
        _formUiState.update { it.copy(showCurrencyDropdown = show) }
    }

    fun togglePaymentMethodDropdown(show: Boolean) {
        _formUiState.update { it.copy(showPaymentMethodDropdown = show) }
    }

    fun togglePersonsDropdown(show: Boolean) {
        _formUiState.update { it.copy(showPersonsDropdown = show) }
    }

    fun toggleDatePicker(show: Boolean) {
        _formUiState.update { it.copy(showDatePicker = show) }
    }

    // Split related handlers
    fun updateSplitType(type: SplitType) {
        _splitType.value = type
        recalculateAmounts()
    }

    fun toggleMemberIncluded(index: Int, included: Boolean) {
        val updatedMembers = _groupMembers.value.toMutableList()
        updatedMembers[index] = updatedMembers[index].copy(included = included)
        _groupMembers.value = updatedMembers
        recalculateAmounts()
    }

    fun updateMemberPercentage(index: Int, percentage: Int) {
        val updatedMembers = _groupMembers.value.toMutableList()
        updatedMembers[index] = updatedMembers[index].copy(percentage = percentage)
        _groupMembers.value = updatedMembers
    }

    fun updateMemberAmount(index: Int, amount: Double) {
        val updatedMembers = _groupMembers.value.toMutableList()
        updatedMembers[index] = updatedMembers[index].copy(amount = amount)
        _groupMembers.value = updatedMembers
    }

    fun addMember(name: String) {
        val updatedMembers = _groupMembers.value.toMutableList()
        val member = _tripMembers.value.find { it.name == name }
        updatedMembers.add(
            GroupMember(
                name = name,
                included = true,
                percentage = 0,
                amount = 0.0,
                userId = member?.id
            )
        )
        _groupMembers.value = updatedMembers
        recalculateAmounts()
    }

    fun removeMember(index: Int) {
        if (_groupMembers.value.size <= 1) return
        val updatedMembers = _groupMembers.value.toMutableList()
        updatedMembers.removeAt(index)
        _groupMembers.value = updatedMembers
        recalculateAmounts()
    }

    // Business logic
    private fun recalculateAmounts() {
        val totalAmount = _expenseState.value.amount.toDoubleOrNull() ?: 0.0
        val includedMembers = _groupMembers.value.filter { it.included }

        if (includedMembers.isEmpty()) return

        when (_splitType.value) {
            SplitType.EQUAL -> {
                val equalAmount = totalAmount / includedMembers.size
                val updatedMembers = _groupMembers.value.map { member ->
                    if (member.included) member.copy(amount = equalAmount) else member
                }
                _groupMembers.value = updatedMembers
            }

            SplitType.PERCENTAGE -> {
                val updatedMembers = _groupMembers.value.map { member ->
                    if (member.included) {
                        val amount = totalAmount * member.percentage / 100.0
                        member.copy(amount = amount)
                    } else {
                        member
                    }
                }
                _groupMembers.value = updatedMembers
            }

            SplitType.CUSTOM -> {
                // Manual doesn't need recalculation, amounts are set directly
            }
        }
    }

    fun isPercentageValid(): Boolean {
        val totalPercentage = _groupMembers.value
            .filter { it.included }
            .sumOf { it.percentage }
        return totalPercentage == 100
    }

    fun isManualAmountValid(): Boolean {
        val totalAmount = _expenseState.value.amount.toDoubleOrNull() ?: 0.0
        val totalManual = _groupMembers.value
            .filter { it.included }
            .sumOf { it.amount }
        return abs(totalAmount - totalManual) < 0.01
    }

    private fun validateExpense(): Boolean {
        val nameValid = _expenseState.value.name.isNotBlank()
        val amountValid = _expenseState.value.amount.isNotBlank() &&
                _expenseState.value.amount.toDoubleOrNull() != null &&
                _expenseState.value.amount.toDoubleOrNull()!! > 0

        // Update error states
        _expenseState.update {
            it.copy(
                nameError = if (nameValid) null else "Name is required",
                amountError = when {
                    _expenseState.value.amount.isBlank() -> "Amount is required"
                    _expenseState.value.amount.toDoubleOrNull() == null -> "Invalid amount"
                    _expenseState.value.amount.toDoubleOrNull()!! <= 0 -> "Amount must be positive"
                    else -> null
                }
            )
        }

        return nameValid && amountValid && when (_splitType.value) {
            SplitType.PERCENTAGE -> isPercentageValid()
            SplitType.CUSTOM -> isManualAmountValid()
            else -> true
        }
    }

    // Convenience function for backward compatibility
    fun saveExpense() {
        createExpense()
    }

    fun getCurrentUserId(): Int {
        return _currentUserId.value ?: 1
    }

    fun clearErrorMessage() {
        _formUiState.update { it.copy(errorMessage = null) }
        _uiState.value = ExpensesUiState.Idle
    }

    fun clearSelectedExpense() {
        _selectedExpense.value = null
    }

    fun resetState() {
        _uiState.value = ExpensesUiState.Idle
        _formUiState.value = UIState()
        _expenseState.value = ExpenseState()
    }
}

enum class SplitType {
    EQUAL, PERCENTAGE, CUSTOM
}

data class GroupMember(
    val name: String,
    val included: Boolean,
    val percentage: Int,
    val amount: Double,
    val userId: Int? = null
)

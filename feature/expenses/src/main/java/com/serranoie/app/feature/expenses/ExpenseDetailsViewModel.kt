package com.serranoie.app.feature.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serranoie.app.feature.expenses.util.ExpenseCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class ExpenseDetailsViewModel : ViewModel() {

    // Data classes for state management
    data class ExpenseState(
        val name: String = "",
        val amount: String = "",
        val date: String = LocalDate.now().format(DateTimeFormatter.ISO_DATE),
        val category: ExpenseCategory = ExpenseCategory.FOOD,
        val currency: String = "EUR",
        val paidBy: String = "Me",
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

    // State flows
    private val _expenseState = MutableStateFlow(ExpenseState())
    val expenseState = _expenseState.asStateFlow()

    private val _uiState = MutableStateFlow(UIState())
    val uiState = _uiState.asStateFlow()

    private val _splitType = MutableStateFlow(SplitType.EQUAL)
    val splitType = _splitType.asStateFlow()

    private val _groupMembers = MutableStateFlow(
        listOf(
            GroupMember("Me", true, percentage = 20, amount = 0.0),
            GroupMember("Alex", true, percentage = 20, amount = 0.0),
            GroupMember("Sarah", true, percentage = 20, amount = 0.0),
            GroupMember("John", true, percentage = 20, amount = 0.0),
            GroupMember("Maria", true, percentage = 20, amount = 0.0)
        )
    )
    val groupMembers = _groupMembers.asStateFlow()

    // Available options
    val persons = listOf(
        "Me",
        "Alex",
        "Sarah",
        "John",
        "Maria",
        "David",
        "Emma"
    )

    val categories = listOf(
        "Food & Drinks",
        "Accommodation",
        "Transportation",
        "Activities",
        "Shopping",
        "Other"
    )
    val currencies = listOf("EUR", "USD", "GBP", "JPY", "CAD", "AUD")
    val paymentMethods =
        listOf("Cash", "Credit Card", "Debit Card","Bank Transfer", "Other")

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

        // Update equal split amounts
        if (amountError == null) {
            recalculateAmounts()
        }
    }

    fun updateDate(date: String) {
        _expenseState.update { it.copy(date = date) }
    }

    fun updateCategory(category: ExpenseCategory) {
        _expenseState.update { it.copy(category = category) }
        _uiState.update { it.copy(showCategoryDropdown = false) }
    }

    fun updateCurrency(currency: String) {
        _expenseState.update { it.copy(currency = currency) }
        _uiState.update { it.copy(showCurrencyDropdown = false) }
    }

    fun updatePaidBy(paidBy: String) {
        _expenseState.update { it.copy(paidBy = paidBy) }
    }

    fun updatePaymentMethod(method: String) {
        _expenseState.update { it.copy(paymentMethod = method) }
        _uiState.update { it.copy(showPaymentMethodDropdown = false) }
    }

    fun updateNotes(notes: String) {
        _expenseState.update { it.copy(notes = notes) }
    }

    // UI state handlers
    fun toggleCategoryDropdown(show: Boolean) {
        _uiState.update { it.copy(showCategoryDropdown = show) }
    }

    fun toggleCurrencyDropdown(show: Boolean) {
        _uiState.update { it.copy(showCurrencyDropdown = show) }
    }

    fun togglePaymentMethodDropdown(show: Boolean) {
        _uiState.update { it.copy(showPaymentMethodDropdown = show) }
    }

    fun togglePersonsDropdown(show: Boolean) {
        _uiState.update { it.copy(showPersonsDropdown = show) }
    }

    fun toggleDatePicker(show: Boolean) {
        _uiState.update { it.copy(showDatePicker = show) }
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
        updatedMembers.add(GroupMember(name, true, percentage = 0, amount = 0.0))
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

            SplitType.MANUAL -> {
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

    fun validateExpense(): Boolean {
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
            SplitType.MANUAL -> isManualAmountValid()
            else -> true
        }
    }

    fun saveExpense() {
        if (!validateExpense()) {
            _uiState.update { it.copy(errorMessage = "Please correct the errors before saving") }
            return
        }

        _expenseState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            // Simulate network request
            try {
                // In a real app, you would save to repository here
                kotlinx.coroutines.delay(1000)
                _uiState.update { it.copy(showSuccessMessage = true, errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to save: ${e.message}") }
            } finally {
                _expenseState.update { it.copy(isSaving = false) }
            }
        }
    }

    // Clear error message
    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
package com.example.spendai.viewmodels

import androidx.lifecycle.ViewModel
import com.example.spendai.models.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TransactionViewModel : ViewModel() {
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    init {
        // Add dummy data
        _transactions.value = listOf(
            Transaction(name = "Groceries", category = "Food", date = "11/25/2025", amount = -150.00, type = "Expense"),
            Transaction(name = "Bus Pass", category = "Transportation", date = "11/22/2025", amount = -60.00, type = "Expense"),
            Transaction(name = "Online Course", category = "Education", date = "11/20/2025", amount = -200.00, type = "Expense"),
            Transaction(name = "Freelance Project", category = "Income", date = "11/18/2025", amount = 750.00, type = "Income"),
            Transaction(name = "Lunch", category = "Food", date = "11/15/2025", amount = -20.00, type = "Expense")
        )
    }

    fun addTransaction(transaction: Transaction) {
        _transactions.update { currentList ->
            listOf(transaction) + currentList
        }
    }

    fun isDuplicate(newTx: Transaction): Boolean {
        return _transactions.value.any { it.name == newTx.name && it.amount == newTx.amount && it.date == newTx.date }
    }
}

package com.example.spendai.models

import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String,
    val date: String,
    val amount: Double,
    val type: String // "Income" or "Expense"
)

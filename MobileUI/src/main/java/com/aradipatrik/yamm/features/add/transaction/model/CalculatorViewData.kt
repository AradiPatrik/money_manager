package com.aradipatrik.yamm.features.add.transaction.model

enum class Action { CalculateResult, AddTransaction }
enum class TransactionType { Income, Expense }

data class CalculatorViewData(
    val numberDisplay: String,
    val action: Action,
    val transactionType: TransactionType
)

package com.aradipatrik.yamm.features.add.transaction.model

enum class CalculatorAction { CalculateResult, AddTransaction }
enum class TransactionType { Income, Expense }

data class CalculatorViewData(
    val numberDisplay: String,
    val calculatorAction: CalculatorAction,
    val transactionType: TransactionType
)

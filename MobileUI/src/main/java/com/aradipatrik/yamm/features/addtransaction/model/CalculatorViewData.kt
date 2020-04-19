package com.aradipatrik.yamm.features.addtransaction.model

enum class CalculatorAction { CalculateResult, AddTransaction }
enum class TransactionType { Income, Expense }

data class CalculatorViewData(
    val numberDisplay: String,
    val calculatorAction: CalculatorAction,
    val transactionType: TransactionType,
    val memo: String
)

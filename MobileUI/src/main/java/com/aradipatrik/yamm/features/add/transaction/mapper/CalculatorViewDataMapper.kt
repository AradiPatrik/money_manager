package com.aradipatrik.yamm.features.add.transaction.mapper

import com.aradipatrik.presentation.viewmodels.add.transaction.AddTransactionState
import com.aradipatrik.presentation.viewmodels.add.transaction.CalculatorState.AddOperation
import com.aradipatrik.presentation.viewmodels.add.transaction.CalculatorState.SingleValue
import com.aradipatrik.presentation.viewmodels.add.transaction.CalculatorState.SubtractOperation
import com.aradipatrik.yamm.features.add.transaction.model.CalculatorAction
import com.aradipatrik.yamm.features.add.transaction.model.CalculatorViewData
import com.aradipatrik.yamm.features.add.transaction.model.TransactionType

class CalculatorViewDataMapper {
    fun mapToViewData(state: AddTransactionState) = CalculatorViewData(
        numberDisplay = when (val calculatorState = state.calculatorState) {
            is SingleValue -> "${calculatorState.value}"
            is AddOperation -> "${calculatorState.lhs} + ${calculatorState.rhs ?: ""}"
            is SubtractOperation -> "${calculatorState.lhs} - ${calculatorState.rhs ?: ""}"
        },
        calculatorAction = when (state.calculatorState) {
            is SingleValue -> CalculatorAction.AddTransaction
            is AddOperation, is SubtractOperation -> CalculatorAction.CalculateResult
        },
        transactionType = TransactionType.Expense,
        memo = state.memo
    )
}

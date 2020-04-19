package com.aradipatrik.yamm.features.addtransaction.mapper

import com.aradipatrik.presentation.viewmodels.addtransaction.AddTransactionState
import com.aradipatrik.presentation.viewmodels.addtransaction.CalculatorState.AddOperation
import com.aradipatrik.presentation.viewmodels.addtransaction.CalculatorState.SingleValue
import com.aradipatrik.presentation.viewmodels.addtransaction.CalculatorState.SubtractOperation
import com.aradipatrik.yamm.features.addtransaction.model.CalculatorAction
import com.aradipatrik.yamm.features.addtransaction.model.CalculatorViewData
import com.aradipatrik.yamm.features.addtransaction.model.TransactionType

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

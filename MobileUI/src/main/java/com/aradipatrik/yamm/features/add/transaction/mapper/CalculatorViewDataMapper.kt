package com.aradipatrik.yamm.features.add.transaction.mapper

import com.aradipatrik.presentation.viewmodels.add.transaction.AddTransactionState
import com.aradipatrik.presentation.viewmodels.add.transaction.CalculatorState
import com.aradipatrik.presentation.viewmodels.add.transaction.CalculatorState.SingleValue
import com.aradipatrik.yamm.features.add.transaction.model.Action
import com.aradipatrik.yamm.features.add.transaction.model.CalculatorViewData
import com.aradipatrik.yamm.features.add.transaction.model.TransactionType

class CalculatorViewDataMapper {
    fun mapToViewData(state: AddTransactionState) = CalculatorViewData(
        numberDisplay = when (val calculatorState = state.calculatorState) {
            is SingleValue -> "${calculatorState.value}"
            is CalculatorState.AddOperation -> TODO()
            is CalculatorState.SubtractOperation -> TODO()
        },
        action = Action.AddTransaction,
        transactionType = TransactionType.Expense
    )
}
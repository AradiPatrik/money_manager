package com.aradipatrik.yamm.util

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Uninitialized
import com.aradipatrik.presentation.presentations.CategoryPresentation
import com.aradipatrik.presentation.presentations.TransactionPresentation
import com.aradipatrik.presentation.viewmodels.add.transaction.AddTransactionState
import com.aradipatrik.presentation.viewmodels.add.transaction.CalculatorState
import com.aradipatrik.presentation.viewmodels.add.transaction.CalculatorState.SingleValue
import com.aradipatrik.testing.CommonMocks.date
import com.aradipatrik.testing.CommonMocks.int
import com.aradipatrik.testing.CommonMocks.string
import org.joda.time.DateTime

object PresentationLayerMocks {
    fun categoryPresentation(
        id: String = string(),
        iconId: String = string(),
        categoryName: String = string()
    ) = CategoryPresentation(id, categoryName, iconId)

    fun transactionPresentation(
        id: String = string(),
        amount: Int = int(),
        categoryPresentation: CategoryPresentation = categoryPresentation(),
        date: DateTime = date(),
        memo: String = string()
    ) = TransactionPresentation(id, amount, categoryPresentation, date, memo)

    fun addTransactionState(
        calculatorState: CalculatorState = SingleValue(int()),
        memo: String = string(),
        addTransactionRequest: Async<Unit> = Uninitialized,
        categoryList: List<CategoryPresentation> = emptyList(),
        categoryListRequest: Async<List<CategoryPresentation>> = Uninitialized,
        isExpense: Boolean = false,
        selectedCategory: CategoryPresentation? = null
    ) = AddTransactionState(
        calculatorState = calculatorState,
        memo = memo,
        addTransactionRequest = addTransactionRequest,
        categoryList = categoryList,
        categoryListRequest = categoryListRequest,
        isExpense = isExpense,
        selectedCategory = selectedCategory,
        selectedDate = date()
    )
}
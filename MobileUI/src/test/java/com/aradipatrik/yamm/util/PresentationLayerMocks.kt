package com.aradipatrik.yamm.util

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Uninitialized
import com.aradipatrik.presentation.presentations.CategoryPresentationModel
import com.aradipatrik.presentation.presentations.TransactionPresentationModel
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
    ) = CategoryPresentationModel(id, categoryName, iconId)

    fun transactionPresentation(
        id: String = string(),
        amount: Int = int(),
        categoryPresentationModel: CategoryPresentationModel = categoryPresentation(),
        date: DateTime = date(),
        memo: String = string()
    ) = TransactionPresentationModel(id, amount, categoryPresentationModel, date, memo)

    fun addTransactionState(
        calculatorState: CalculatorState = SingleValue(int()),
        memo: String = string(),
        addTransactionRequest: Async<Unit> = Uninitialized,
        categoryListModel: List<CategoryPresentationModel> = emptyList(),
        categoryListRequestModel: Async<List<CategoryPresentationModel>> = Uninitialized,
        isExpense: Boolean = false,
        selectedCategoryModel: CategoryPresentationModel? = null
    ) = AddTransactionState(
        calculatorState = calculatorState,
        memo = memo,
        addTransactionRequest = addTransactionRequest,
        categoryListModel = categoryListModel,
        categoryListRequestModel = categoryListRequestModel,
        isExpense = isExpense,
        selectedCategoryModel = selectedCategoryModel,
        selectedDate = date()
    )
}
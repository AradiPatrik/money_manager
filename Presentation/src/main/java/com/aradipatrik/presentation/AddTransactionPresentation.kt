package com.aradipatrik.presentation

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Uninitialized
import com.aradipatrik.presentation.presentations.CategoryPresentation

data class AddTransactionState(
    val amount: Int = 0,
    val isExpense: Boolean = false,
    val categoryList: List<CategoryPresentation> = emptyList(),
    val categoryListRequest: Async<List<CategoryPresentation>> = Uninitialized,
    val selectedCategory: CategoryPresentation
)
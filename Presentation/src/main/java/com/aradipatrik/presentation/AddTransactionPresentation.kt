package com.aradipatrik.presentation

import com.airbnb.mvrx.*
import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.domain.usecase.AddTransaction
import com.aradipatrik.domain.usecase.GetCategories
import com.aradipatrik.presentation.common.MvRxViewModel
import com.aradipatrik.presentation.mapper.CategoryPresentationMapper
import com.aradipatrik.presentation.presentations.CategoryPresentation
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import org.koin.android.ext.android.inject

data class AddTransactionState(
    val amount: Int = 0,
    val isExpense: Boolean = true,
    val memo: String = "",
    val selectedDate: DateTime = DateTime.now(),
    val categoryList: List<CategoryPresentation> = emptyList(),
    val categoryListRequest: Async<List<CategoryPresentation>> = Uninitialized,
    val addTransactionRequest: Async<Unit> = Uninitialized,
    val selectedCategory: CategoryPresentation? = null
) : MvRxState

class AddTransactionViewModel(
    initialState: AddTransactionState,
    getCategories: GetCategories,
    private val addTransaction: AddTransaction,
    private val mapper: CategoryPresentationMapper
) : MvRxViewModel<AddTransactionState>(initialState) {
    companion object : MvRxViewModelFactory<AddTransactionViewModel, AddTransactionState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: AddTransactionState
        ): AddTransactionViewModel? {
            val addTransactionUseCase: AddTransaction by viewModelContext.activity.inject()
            val getCategoriesUseCase: GetCategories by viewModelContext.activity.inject()
            val mapper: CategoryPresentationMapper by viewModelContext.activity.inject()
            return AddTransactionViewModel(
                state, getCategoriesUseCase, addTransactionUseCase, mapper
            )
        }
    }

    init {
        logStateChanges()
        getCategories.get()
            .map { it.map(mapper::mapToPresentation) }
            .subscribeOn(Schedulers.io())
            .execute {
                copy(
                    categoryListRequest = it,
                    categoryList = it() ?: emptyList(),
                    selectedCategory = it()?.get(0)
                )
            }
    }

    fun addTransaction() = withState { state ->
        if (state.selectedCategory != null) {
            addTransaction.get(
                AddTransaction.Params.forTransaction(
                    Transaction(
                        id = "",
                        category = mapper.mapFromPresentation(state.selectedCategory),
                        amount = state.amount,
                        memo = state.memo,
                        date = state.selectedDate
                    )
                )
            )
                .subscribeOn(Schedulers.io())
                .execute {
                    val initialState = AddTransactionState()
                    copy(
                        addTransactionRequest = it,
                        selectedDate = initialState.selectedDate,
                        isExpense = initialState.isExpense,
                        amount = initialState.amount,
                        memo = initialState.memo
                    )
                }
        }
    }
}

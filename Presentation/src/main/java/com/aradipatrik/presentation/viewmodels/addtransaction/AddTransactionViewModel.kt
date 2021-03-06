package com.aradipatrik.presentation.viewmodels.addtransaction

import com.airbnb.mvrx.*
import com.aradipatrik.domain.interactor.category.GetCategoriesInteractor
import com.aradipatrik.domain.interactor.transaction.AddTransactionInteractor
import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.presentation.common.MvRxViewModel
import com.aradipatrik.presentation.common.ViewEventProcessor
import com.aradipatrik.presentation.common.appendDigit
import com.aradipatrik.presentation.common.withLastDigitRemoved
import com.aradipatrik.presentation.mapper.CategoryPresentationMapper
import com.aradipatrik.presentation.presentations.CategoryPresentationModel
import com.aradipatrik.presentation.viewmodels.addtransaction.AddTransactionViewEvent.*
import com.aradipatrik.presentation.viewmodels.addtransaction.CalculatorState.*
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import org.koin.android.ext.android.inject

interface BinaryOperation {
    val lhs: Int
    val rhs: Int?
}

sealed class CalculatorState {
    abstract val value: Int

    data class SingleValue(override val value: Int) : CalculatorState()
    data class AddOperation(
        override val lhs: Int,
        override val rhs: Int?
    ) : BinaryOperation, CalculatorState() {
        override val value get() = lhs + (rhs ?: 0)
    }

    data class SubtractOperation(
        override val lhs: Int,
        override val rhs: Int?
    ) : BinaryOperation, CalculatorState() {
        override val value get() = lhs - (rhs ?: 0)
    }
}

data class AddTransactionState(
    val calculatorState: CalculatorState = SingleValue(0),
    val isExpense: Boolean = true,
    val memo: String = "",
    val selectedDate: DateTime = DateTime.now(),
    val categoryListModel: List<CategoryPresentationModel> = emptyList(),
    val categoryListRequestModel: Async<List<CategoryPresentationModel>> = Uninitialized,
    val addTransactionRequest: Async<Unit> = Uninitialized,
    val selectedCategoryModel: CategoryPresentationModel? = null
) : MvRxState

class AddTransactionViewModel(
    initialState: AddTransactionState,
    getCategoriesInteractor: GetCategoriesInteractor,
    private val addTransactionInteractor: AddTransactionInteractor,
    private val mapper: CategoryPresentationMapper
) : MvRxViewModel<AddTransactionState>(initialState), ViewEventProcessor<AddTransactionViewEvent> {
    companion object : MvRxViewModelFactory<AddTransactionViewModel, AddTransactionState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: AddTransactionState
        ): AddTransactionViewModel? {
            val addTransactionInteractorUseCase: AddTransactionInteractor by viewModelContext.activity.inject()
            val getCategoriesInteractorUseCase: GetCategoriesInteractor by viewModelContext.activity.inject()
            val mapper: CategoryPresentationMapper by viewModelContext.activity.inject()
            return AddTransactionViewModel(
                state, getCategoriesInteractorUseCase, addTransactionInteractorUseCase, mapper
            )
        }
    }

    init {
        getCategoriesInteractor.get()
            .map { it.map(mapper::mapToPresentation) }
            .subscribeOn(Schedulers.io())
            .execute {
                copy(
                    categoryListRequestModel = it,
                    categoryListModel = it() ?: emptyList(),
                    selectedCategoryModel = it()?.get(0)
                )
            }
    }

    override fun processEvent(event: AddTransactionViewEvent) = when (event) {
        is NumberClick -> appendDigit(event.number)
        is MemoChange -> updateMemo(event.memo)
        ActionClick -> withState { state ->
            when (state.calculatorState) {
                is SingleValue -> addTransaction(state)
                else -> calculateValueOfOperation()
            }
        }
        DeleteOneClick -> deleteOne()
        PlusClick -> add()
        MinusClick -> subtract()
        EqualsClick -> calculateValueOfOperation()
        PointClick -> TODO("Add support for floating point transactions")
    }

    private fun updateMemo(newValue: String) = setState { copy(memo = newValue) }

    private fun calculateValueOfOperation() = setState {
        copy(
            calculatorState = when (calculatorState) {
                is SingleValue -> throw IllegalStateException("Equals clicked on single value")
                is AddOperation -> SingleValue(calculatorState.value)
                is SubtractOperation -> SingleValue(calculatorState.value)
            }
        )
    }

    private fun appendDigit(digit: Int) = setState {
        copy(
            calculatorState = when (calculatorState) {
                is SingleValue -> SingleValue(
                    appendToRhs(calculatorState.value, digit)
                )
                is AddOperation -> AddOperation(
                    calculatorState.lhs,
                    appendToRhs(calculatorState.rhs, digit)
                )
                is SubtractOperation -> SubtractOperation(
                    calculatorState.lhs,
                    appendToRhs(calculatorState.rhs, digit)
                )
            }
        )
    }

    private fun appendToRhs(rhs: Int?, digit: Int): Int = when (rhs) {
        null, 0 -> digit
        else -> rhs.appendDigit(digit)
    }

    private fun deleteOne() = setState {
        copy(
            calculatorState = when (calculatorState) {
                is SingleValue -> SingleValue(calculatorState.value.withLastDigitRemoved)
                is AddOperation -> deleteOneFromAddOperation(calculatorState)
                is SubtractOperation -> deleteOneFromSubtractOperation(calculatorState)
            }
        )
    }

    private fun deleteOneFromAddOperation(
        calculatorState: AddOperation
    ) = if (calculatorState.rhs == null) {
        SingleValue(calculatorState.lhs)
    } else {
        AddOperation(
            calculatorState.lhs,
            deleteOneDigitFromRhs(calculatorState.rhs)
        )
    }

    private fun deleteOneFromSubtractOperation(
        calculatorState: SubtractOperation
    ) = if (calculatorState.rhs == null) {
        SingleValue(calculatorState.lhs)
    } else {
        SubtractOperation(
            calculatorState.lhs,
            deleteOneDigitFromRhs(calculatorState.rhs)
        )
    }

    private fun deleteOneDigitFromRhs(rhs: Int) = when (rhs.withLastDigitRemoved) {
        0 -> null
        else -> rhs.withLastDigitRemoved
    }

    private fun subtract() = setState {
        copy(calculatorState = SubtractOperation(calculatorState.value, null))
    }

    private fun add() = setState {
        copy(calculatorState = AddOperation(calculatorState.value, null))
    }

    private fun addTransaction(state: AddTransactionState) {
        if (state.selectedCategoryModel != null) {
            require(state.calculatorState is SingleValue)
            addTransactionInteractor.get(
                AddTransactionInteractor.Params.forTransaction(
                    Transaction(
                        id = "",
                        category = mapper.mapFromPresentation(state.selectedCategoryModel),
                        amount = state.calculatorState.value,
                        memo = state.memo,
                        date = state.selectedDate
                    )
                )
            )
                .subscribeOn(Schedulers.io())
                .execute {
                    copy(addTransactionRequest = it)
                }
        }
    }
}

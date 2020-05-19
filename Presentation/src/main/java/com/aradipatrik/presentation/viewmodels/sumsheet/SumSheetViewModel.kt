package com.aradipatrik.presentation.viewmodels.sumsheet

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.aradipatrik.domain.interactor.selectedmonth.DecrementSelectedMonthInteractor
import com.aradipatrik.domain.interactor.selectedmonth.GetSelectedMonthInteractor
import com.aradipatrik.domain.interactor.selectedmonth.IncrementSelectedMonthInteractor
import com.aradipatrik.domain.interactor.transaction.GetTransactionsInIntervalInteractor
import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.presentation.common.MvRxViewModel
import org.joda.time.Interval
import org.joda.time.YearMonth

data class SumSheetState(
    val getTransactionsOperation: Async<List<Transaction>> = Uninitialized,
    val incomeThisMonth: Int? = null,
    val expenseThisMonth: Int? = null,
    val grandTotal: Int? = null,
    val monthlyTotal: Int? = null
) : MvRxState

class SumSheetViewModel(
    initialState: SumSheetState,
    getSelectedMonthInteractor: GetSelectedMonthInteractor,
    incrementSelectedMonthInteractor: IncrementSelectedMonthInteractor,
    decrementSelectedMonthInteractor: DecrementSelectedMonthInteractor,
    getTransactionsInIntervalInteractor: GetTransactionsInIntervalInteractor
) : MvRxViewModel<SumSheetState>(initialState) {
    init {
        getSelectedMonthInteractor.get()
            .map { Interval(YearMonth(it).toInterval()) }
            .switchMap { interval ->
                getTransactionsInIntervalInteractor.get(
                    GetTransactionsInIntervalInteractor.Params.forInterval(interval)
                ).map { transactions -> transactions to interval}
            }
            .execute {
                copy(

                )
            }

    }
}
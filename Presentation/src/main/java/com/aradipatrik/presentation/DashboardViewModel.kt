package com.aradipatrik.presentation

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.aradipatrik.presentation.common.MvRxViewModel
import com.aradipatrik.presentation.presentations.TransactionPresentation
import org.joda.time.YearMonth

data class DashboardState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val transactionsInSelectedMonth: Async<List<TransactionPresentation>> = Uninitialized
): MvRxState

class DashboardViewModel(initialState: DashboardState) : MvRxViewModel<DashboardState>(initialState) {
    fun refreshCurrentMonth() = setState {
        copy(

        )
    }
}

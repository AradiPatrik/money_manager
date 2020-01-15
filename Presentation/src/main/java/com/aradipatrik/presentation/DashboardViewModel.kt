package com.aradipatrik.presentation

import com.airbnb.mvrx.*
import com.aradipatrik.domain.usecase.GetTransactionsInInterval
import com.aradipatrik.presentation.common.MvRxViewModel
import com.aradipatrik.presentation.injection.DaggerApplication
import com.aradipatrik.presentation.mapper.TransactionPresentationMapper
import com.aradipatrik.presentation.presentations.TransactionPresentation
import org.joda.time.Interval
import org.joda.time.YearMonth

data class DashboardState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val transactionsInSelectedMonth: Async<List<TransactionPresentation>> = Uninitialized
) : MvRxState {
    val selectedMonthAsInterval: Interval = selectedMonth.toInterval()
}

class DashboardViewModel(
    initialState: DashboardState,
    internal val transactionMapper: TransactionPresentationMapper,
    internal val getTransactionsInInterval: GetTransactionsInInterval
) : MvRxViewModel<DashboardState>(initialState) {
    companion object : MvRxViewModelFactory<DashboardViewModel, DashboardState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: DashboardState
        ): DashboardViewModel? {
            val useCaseContainer = viewModelContext.app<DaggerApplication>().useCaseContainer
            val mapperContainer = viewModelContext.app<DaggerApplication>().mapperContainer
            val getTransactionsInInterval = useCaseContainer.getTransactionsInInterval
            val transactionMapper = mapperContainer.mapper
            return DashboardViewModel(state, transactionMapper, getTransactionsInInterval)
        }
    }

    fun refreshCurrentMonth() = withState { state ->
        getTransactionsInInterval.get(
            GetTransactionsInInterval.Params(Interval(state.selectedMonthAsInterval))
        )
            .map { transactions -> transactions.map(transactionMapper::mapToPresentation) }
            .execute { copy(transactionsInSelectedMonth = it) }
    }
}
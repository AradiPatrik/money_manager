package com.aradipatrik.presentation

import com.airbnb.mvrx.*
import com.aradipatrik.domain.usecase.GetTransactionsInInterval
import com.aradipatrik.presentation.common.MvRxViewModel
import com.aradipatrik.presentation.mapper.TransactionPresentationMapper
import com.aradipatrik.presentation.presentations.TransactionPresentation
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import org.joda.time.Interval
import org.joda.time.YearMonth
import org.koin.android.ext.android.inject

data class DashboardState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val transactionsOfSelectedMonth: List<TransactionPresentation> = emptyList(),
    val request: Async<List<TransactionPresentation>> = Uninitialized
) : MvRxState {
    val selectedMonthAsInterval: Interval = selectedMonth.toInterval()
    val datesToTransactions = transactionsOfSelectedMonth
        .groupBy { it.date.toLocalDate() }
        .mapValues { (_, value) -> value.toSortedSet(compareBy(TransactionPresentation::date)) }
        .toSortedMap()
}

class DashboardViewModel(
    initialState: DashboardState,
    private val transactionMapper: TransactionPresentationMapper,
    private val getTransactionsInInterval: GetTransactionsInInterval
) : MvRxViewModel<DashboardState>(initialState) {
    internal var currentRequestDisposable: Disposable = Disposables.empty()

    companion object : MvRxViewModelFactory<DashboardViewModel, DashboardState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: DashboardState
        ): DashboardViewModel? {
            val useCase: GetTransactionsInInterval by viewModelContext.activity.inject()
            val mapper: TransactionPresentationMapper by viewModelContext.activity.inject()
            return DashboardViewModel(state, mapper, useCase)
        }
    }

    init {
        fetchCurrentMonth()
    }

    fun fetchCurrentMonth() = withState { state ->
        currentRequestDisposable.dispose()
        currentRequestDisposable = getTransactionsInInterval.get(
            GetTransactionsInInterval.Params(Interval(state.selectedMonthAsInterval))
        )
            .map { transactions -> transactions.map(transactionMapper::mapToPresentation) }
            .subscribeOn(Schedulers.io())
            .execute {
                copy(
                    request = it,
                    transactionsOfSelectedMonth = it() ?: transactionsOfSelectedMonth
                )
            }
    }
}

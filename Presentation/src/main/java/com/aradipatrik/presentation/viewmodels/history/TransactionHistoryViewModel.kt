package com.aradipatrik.presentation.viewmodels.history

import com.airbnb.mvrx.*
import com.aradipatrik.domain.interactor.transaction.GetTransactionsInIntervalInteractor
import com.aradipatrik.presentation.common.MvRxViewModel
import com.aradipatrik.presentation.mapper.TransactionPresentationMapper
import com.aradipatrik.presentation.presentations.TransactionPresentation
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import org.joda.time.Interval
import org.joda.time.YearMonth
import org.koin.android.ext.android.inject

data class TransactionHistoryState(
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

class TransactionHistoryViewModel(
    initialState: TransactionHistoryState,
    private val transactionMapper: TransactionPresentationMapper,
    private val getTransactionsInIntervalInteractor: GetTransactionsInIntervalInteractor
) : MvRxViewModel<TransactionHistoryState>(initialState) {
    internal var currentRequestDisposable: Disposable = Disposables.empty()

    companion object : MvRxViewModelFactory<TransactionHistoryViewModel, TransactionHistoryState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: TransactionHistoryState
        ): TransactionHistoryViewModel? {
            val useCase: GetTransactionsInIntervalInteractor by viewModelContext.activity.inject()
            val mapper: TransactionPresentationMapper by viewModelContext.activity.inject()
            return TransactionHistoryViewModel(
                state,
                mapper,
                useCase
            )
        }
    }

    init {
        logStateChanges()
        fetchCurrentMonth()
    }

    fun fetchCurrentMonth() = withState { state ->
        currentRequestDisposable.dispose()
        currentRequestDisposable = getTransactionsInIntervalInteractor.get(
            GetTransactionsInIntervalInteractor.Params(Interval(state.selectedMonthAsInterval))
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

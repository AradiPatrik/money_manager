package com.aradipatrik.domain.interactor.stats

import com.aradipatrik.domain.interfaces.data.ExpenseStatsRepository
import com.aradipatrik.domain.interfaces.data.WalletRepository
import com.aradipatrik.domain.model.ExpenseStats
import com.aradipatrik.domain.usecase.ObservableUseCase
import io.reactivex.Observable
import org.joda.time.YearMonth

class GetMonthlyExpenseStatsInteractor(
    val walletRepository: WalletRepository,
    val expenseStatsRepository: ExpenseStatsRepository
) : ObservableUseCase<ExpenseStats, GetMonthlyExpenseStatsInteractor.Params> {
    data class Params(val yearMonth: YearMonth) {
        companion object {
            fun forSelectedMonth(yearMonth: YearMonth) = Params(yearMonth)
        }
    }

    override fun get(params: Params?): Observable<ExpenseStats> {
        require(params != null) { "Selected month should not be null" }
        return walletRepository.getSelectedWallet()
            .flatMapObservable {
                expenseStatsRepository.getExpenseStatsOfMonth(params.yearMonth, it.id)
            }
    }
}
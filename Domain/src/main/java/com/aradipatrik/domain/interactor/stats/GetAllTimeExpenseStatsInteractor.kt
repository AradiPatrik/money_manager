package com.aradipatrik.domain.interactor.stats

import com.aradipatrik.domain.interfaces.data.ExpenseStatsRepository
import com.aradipatrik.domain.interfaces.data.WalletRepository
import com.aradipatrik.domain.model.ExpenseStats
import com.aradipatrik.domain.usecase.ObservableUseCase

class GetAllTimeExpenseStatsInteractor(
    private val walletRepository: WalletRepository,
    private val expenseStatsRepository: ExpenseStatsRepository
) : ObservableUseCase<ExpenseStats, Unit> {
    override fun get(params: Unit?) = walletRepository.getSelectedWallet()
        .flatMapObservable {
            expenseStatsRepository.getAlltimeExpenseStats(it.id)
        }
}
